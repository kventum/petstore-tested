package api.pets;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import petstore.models.ErrorBody;
import petstore.models.pets.Pet;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;
import static petstore.steps.PetSteps.getPetById;

public class GetPetByIdTest {

    @Test
    @Tag(POSITIVE)
    public void getExistedPetById() {
        long id = 12;

        Pet pet = getPetById(id, SC_OK).as(Pet.class);

        assertAll(
                () -> assertEquals(id, pet.getId())
        );
    }

    @Test
    @Tag(NEGATIVE)
    public void getPetByUnexistedId() {
        long id = 1024;

        ErrorBody response = getPetById(id, SC_NOT_FOUND).as(ErrorBody.class);

        assertAll(
                () -> assertEquals(1, response.getCode()),
                () -> assertEquals("error", response.getType()),
                () -> assertEquals("Pet not found", response.getMessage())
        );
    }

    @Test
    @Tag(NEGATIVE)
    public void getPetByStringId() {
        String id = "i";

        ErrorBody response = getPetById(id, SC_NOT_FOUND).as(ErrorBody.class);

        assertAll(
                () -> assertEquals(404, response.getCode()),
                () -> assertEquals("unknown", response.getType()),
                () -> assertEquals("java.lang.NumberFormatException: For input string: \"" + id + "\"", response.getMessage())
        );
    }
}
