package api.store;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import petstore.models.ApiResponse;
import petstore.models.Order;
import petstore.models.OrderStatus;
import petstore.models.pets.Pet;
import petstore.services.PetService;
import petstore.services.StoreService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Endpoints.ORDER;
import static petstore.constants.Others.*;
import static petstore.services.BaseService.responseSpec;
import static petstore.util.DataGenerator.getOrder;
import static petstore.util.DataGenerator.getPet;

@DisplayName("Тесты на размещение заказа питомца")
public class PlaceOrderTest extends BaseTest {

    private final StoreService storeService = new StoreService();
    private final PetService petService = new PetService();
    private final List<Order> orderList = new ArrayList<>();
    long petId;

    @BeforeAll
    protected void prepare() {
        Pet pet = petService.createPet(getPet("Korgey", "http://photo.url"), SC_OK);
        petId = pet.getId();
    }

    @AfterAll
    protected void clear() {
        petService.deletePet(petId, SC_OK);
        orderList.forEach(order -> storeService.deleteOrder(order.getId(), SC_OK));
    }

    @ParameterizedTest
    @CsvFileSource(delimiter =';', resources = "/store/createOrderWithAllFields.csv", numLinesToSkip = 1)
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Тест на размещение заказа со всеми валидными полями")
    public void createOrderWithAllFields(int quantity, String shipDate, String status, boolean complete) {
        OffsetDateTime expectedDate = OffsetDateTime.parse(shipDate);
        Order requestBody = getOrder(petId, quantity, expectedDate,
                OrderStatus.valueOf(status), complete);
        Order responseBody = storeService.createOrder(requestBody, SC_OK);
        long id = responseBody.getId();

        orderList.add(responseBody);

        assertAll(
                () -> assertNotEquals(0, id, ORDER_ID_ZERO),
                () -> assertEquals(petId, responseBody.getPetId(),PET_ID_WRONG),
                () -> assertEquals(quantity, responseBody.getQuantity(), ORDER_QUANTITY_WRONG),
                () -> assertEquals(expectedDate, responseBody.getShipDate(), ORDER_DATE_WRONG),
                () -> assertEquals(OrderStatus.valueOf(status), responseBody.getStatus(), ORDER_STATUS_WRONG),
                () -> assertEquals(complete, responseBody.isComplete(), ORDER_COMPLETE_WRONG)
        );
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Тест на размещение заказа без опциональных полей")
    public void createOrderWithoutOptionalFields() {

        Order requestBody = getOrder(petId, true);
        Order responseBody = storeService.createOrder(requestBody, SC_OK);
        long id = responseBody.getId();

        orderList.add(responseBody);

        assertAll(
                () -> assertNotEquals(0, id, ORDER_ID_ZERO),
                () -> assertEquals(petId, responseBody.getPetId(), PET_ID_WRONG),
                () -> assertEquals(0, responseBody.getQuantity(), ORDER_QUANTITY_WRONG),
                () -> assertTrue(responseBody.isComplete(), ORDER_COMPLETE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Тест на размещение заказа без питомца")
    public void createOrderWithoutPet() {
        int quantity = 0;
        Order requestBody = getOrder(quantity, OrderStatus.delivered, false);
        Order responseBody = storeService.createOrder(requestBody, SC_OK);
        long id = responseBody.getId();

        orderList.add(responseBody);

        assertAll(
                () -> assertNotEquals(0, id, ORDER_ID_ZERO),
                () -> assertEquals(0, responseBody.getPetId(), PET_ID_WRONG),
                () -> assertEquals(quantity, responseBody.getQuantity(), ORDER_QUANTITY_WRONG),
                () -> assertEquals(OrderStatus.delivered, responseBody.getStatus(), ORDER_STATUS_WRONG),
                () -> assertFalse(responseBody.isComplete(), ORDER_COMPLETE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Тест на отправку массива заказов в теле")
    public void createOrderArray() {
        int quantity = 2;
        OffsetDateTime dateTime = OffsetDateTime.now();
        List<Order> requestBody = List.of(getOrder(petId, quantity, dateTime, OrderStatus.approved, false));
        ApiResponse responseBody = storeService.post(ORDER, requestBody)
                .then().spec(responseSpec(SC_INTERNAL_SERVER_ERROR, ContentType.JSON))
                .extract().as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_INTERNAL_SERVER_ERROR, responseBody.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, responseBody.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(SMTH_BAD, responseBody.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }
}
