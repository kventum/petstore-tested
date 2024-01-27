package api.pets;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import petstore.models.ApiResponse;
import petstore.models.pets.Pet;
import petstore.services.PetService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;
import static petstore.util.DataGenerator.getPet;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeletePetTest {

    private static final String ZERO_CONTENT = "0";
    private final PetService petService = new PetService();
    private Long id;

    @BeforeAll
    public void preparePet() {
        Pet pet = petService.createPet(getPet("Пушишка", ""), SC_OK);
        id = pet.getId();
    }
    @Test
    @Tag(POSITIVE)
    @DisplayName("Удаление существующего питомца")
    public void deleteExistedPet() {
        Response response = petService.deletePet(id, SC_OK);
        ApiResponse body = response.as(ApiResponse.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertEquals(SC_OK, body.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals("unknown", body.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(id.toString(), body.getMessage(), RESPONSE_MESSAGE_WRONG),
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG)
        );

        petService.getPetById(id, SC_NOT_FOUND);
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Удаление несуществующего питомца")
    public void deleteUnexistedPet() {
        long id = 1212;

        Response response = petService.deletePet(id, SC_NOT_FOUND);

        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG),
                () -> assertEquals(ZERO_CONTENT, response.getHeader("Content-Length"), CONTENT_LENGTH_NOT_NULL)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Удаление питомца с невалидным id")
    public void deletePetByStringId() {
        String id = "a";

        Response response = petService.deletePet(id, SC_NOT_FOUND);
        ApiResponse body = response.as(ApiResponse.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG),
                () -> assertEquals(SC_NOT_FOUND, body.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals("unknown", body.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("java.lang.NumberFormatException: For input string: \""+ id + "\"",
                        body.getMessage(),RESPONSE_MESSAGE_WRONG)
        );
    }
}
