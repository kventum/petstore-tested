package api.store;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;
import petstore.models.ApiResponse;
import petstore.models.Order;
import petstore.models.OrderStatus;
import petstore.services.PetService;
import petstore.services.StoreService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getOrder;
import static petstore.util.DataGenerator.getPet;

@DisplayName("Тесты на получение заказа по id")
public class FindOrderTest extends BaseTest {

    private static final int quantity = 10;
    private final StoreService storeService = new StoreService();
    private final PetService petService = new PetService();
    private final OffsetDateTime dateTime = OffsetDateTime.of(LocalDateTime.now().withNano(0), ZoneOffset.UTC);
    private long id;
    private long petId;

    @BeforeAll
    protected void prepare() {
        petId = petService.createPet(getPet("Gav", "http://katzen.de"), SC_OK).getId();
        id = storeService.createOrder(getOrder(petId, quantity, dateTime, OrderStatus.placed, false), SC_OK).getId();
    }

    @AfterAll
    protected void clear() {
        petService.deletePet(petId, SC_OK);
        storeService.deleteOrder(id, SC_OK);
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Поиск заказа по валидному id")
    protected void findOrderById() {
        Order response = storeService.getOrder(id, SC_OK).as(Order.class);

        assertAll(
                () -> assertEquals(id, response.getId(), ORDER_ID_WRONG),
                () -> assertEquals(petId, response.getPetId(), PET_ID_WRONG),
                () -> assertEquals(quantity, response.getQuantity(), ORDER_QUANTITY_WRONG),
                () -> assertEquals(dateTime, response.getShipDate(), ORDER_DATE_WRONG),
                () -> assertEquals(OrderStatus.placed, response.getStatus(), ORDER_STATUS_WRONG),
                () -> assertFalse(response.isComplete(), ORDER_COMPLETE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Поиск заказа по несуществующему id")
    protected void findOrderByNonexistentId() {
        ApiResponse response = storeService.getOrder(0, SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(1, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals("error", response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("Order not found", response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );

    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Поиск заказа по невалидному id")
    protected void findOrderByNotValidId() {
        char id = 'i';
        ApiResponse response = storeService.getOrder(id, SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("java.lang.NumberFormatException: For input string: \"" + id + "\"",
                        response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );

    }
}
