package api.pets;

import api.BaseTest;
import org.junit.jupiter.api.*;
import petstore.models.ApiResponse;
import petstore.models.pets.Pet;
import petstore.services.PetService;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;
import static petstore.util.DataGenerator.getPet;

public class GetPetByIdTest extends BaseTest {

    private final PetService petService = new PetService();
    long id;

    @BeforeAll
    public void prepare() {
        Pet pet = petService.createPet(getPet("Korshik", "-"), SC_OK);
        id = pet.getId();
    }

    @AfterAll
    public void clear() {
        petService.deletePet(id, SC_OK);
    }

    @Test
    @Tag(POSITIVE)
    @DisplayName("Получение питомца по id")
    public void getExistedPetById() {
        Pet pet = petService.getPetById(id, SC_OK).as(Pet.class);

        assertAll(
                () -> assertEquals(id, pet.getId())
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Получение несуществующего питомца по id")
    public void getPetByUnexistedId() {
        long id = 1024;

        ApiResponse response = petService.getPetById(id, SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(1, response.getCode()),
                () -> assertEquals("error", response.getType()),
                () -> assertEquals("Pet not found", response.getMessage())
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Получение питомца по невалидному id")
    public void getPetByStringId() {
        String id = "i";

        ApiResponse response = petService.getPetById(id, SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, response.getCode()),
                () -> assertEquals("unknown", response.getType()),
                () -> assertEquals("java.lang.NumberFormatException: For input string: \"" + id + "\"", response.getMessage())
        );
    }
}
