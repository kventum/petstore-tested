package api.pets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import petstore.models.ErrorBody;
import petstore.models.pets.Pet;
import petstore.services.PetService;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;

public class GetPetByIdTest {

    private final PetService petService = new PetService();

    @Test
    @Tag(POSITIVE)
    @DisplayName("Получение питомца по id")
    public void getExistedPetById() {
        long id = 12;

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

        ErrorBody response = petService.getPetById(id, SC_NOT_FOUND).as(ErrorBody.class);

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

        ErrorBody response = petService.getPetById(id, SC_NOT_FOUND).as(ErrorBody.class);

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, response.getCode()),
                () -> assertEquals("unknown", response.getType()),
                () -> assertEquals("java.lang.NumberFormatException: For input string: \"" + id + "\"", response.getMessage())
        );
    }
}
