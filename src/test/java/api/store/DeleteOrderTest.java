package api.store;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;
import petstore.models.ApiResponse;
import petstore.models.Order;
import petstore.models.OrderStatus;
import petstore.models.pets.Pet;
import petstore.services.PetService;
import petstore.services.StoreService;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getOrder;
import static petstore.util.DataGenerator.getPet;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тесты на удаление заказа")
public class DeleteOrderTest extends BaseTest {

    private final StoreService storeService = new StoreService();
    private final PetService petService = new PetService();
    private Long orderWithPetId;
    private Long orderWithoutPetId;
    private long petId;

    @BeforeAll
    protected void prepare() {
        Pet pet = petService.createPet(getPet("Bernar", "bernar.com"), SC_OK);
        petId = pet.getId();
        Order orderWithPet = storeService.createOrder(getOrder(petId, false), SC_OK);
        Order orderWithoutPet = storeService.createOrder(getOrder(1, OrderStatus.placed, true), SC_OK);
        orderWithPetId = orderWithPet.getId();
        orderWithoutPetId = orderWithoutPet.getId();
    }

    @AfterAll
    protected void clear() {
        petService.deletePet(petId, SC_OK);
    }

    @Test
    @Tag(POSITIVE)
    @org.junit.jupiter.api.Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаление существующего заказа с питомцем")
    public void deleteOrderWithPet() {
        ApiResponse response = storeService.deleteOrder(orderWithPetId, SC_OK).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(orderWithPetId.toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(POSITIVE)
    @org.junit.jupiter.api.Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаление существующего заказа без питомца")
    public void deleteOrderWithoutPet() {
        ApiResponse response = storeService.deleteOrder(orderWithoutPetId, SC_OK).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(orderWithoutPetId.toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Удаление несуществующего заказа")
    public void deleteNonexistentOrder() {
        ApiResponse response = storeService.deleteOrder(orderWithPetId, SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("Order Not Found", response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Удаление заказа с невалидным буквенным id")
    public void deleteOrderByCharId() {
        char id = 'i';
        ApiResponse response = storeService.deleteOrder(id, SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("java.lang.NumberFormatException: For input string: \"" + id + "\"",
                        response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }
}
