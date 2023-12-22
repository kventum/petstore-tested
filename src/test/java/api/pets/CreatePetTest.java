package api.pets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import petstore.models.pets.Category;
import petstore.models.pets.Pet;
import petstore.models.pets.PetStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.steps.PetSteps.createPet;

public class CreatePetTest {

    @Test
    @DisplayName("Создание питомца со всеми валидными полями")
    public void createValidPet() {
        String petName = "Cracker";
        String petCategoryName = "doggy";
        String photoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRTHtUVGo-nC76zpcFVWOvRDxmGNIaP7k6CEA&usqp=CAU";
        String tagName = "dog";
        Pet request = Pet.builder()
                .id((long) (Math.random()*10000))
                .category(new Category(petCategoryName, ((long)(Math.random()*10000))))
                .name(petName)
                .photoUrls(new String[]{photoUrl})
                .tags(List.of(new Category(tagName, (long)(Math.random()*1000))))
                .status(PetStatus.pending)
                .build();

        Pet response = createPet(request);

        assertAll(
                () -> assertNotNull(response.getId(), PET_ID_NULL),
                () -> assertEquals(petCategoryName, response.getCategory().getName(), PET_CATEGORY_NAME_WRONG),
                () -> assertNotNull(response.getCategory().getId(), PET_CATEGORY_ID_NULL),
                () -> assertEquals(petName, response.getName(), PET_NAME_WRONG),
                () -> assertEquals(photoUrl, response.getPhotoUrls()[0], PET_PHOTO_URL_WRONG),
                () -> assertEquals(tagName, response.getTags().get(0).getName(), TAG_NAME_WRONG),
                () -> assertNotNull(response.getTags().get(0).getId(), TAG_ID_NULL),
                () -> assertEquals(PetStatus.pending, response.getStatus(), PET_STATUS_WRONG)
        );
    }
}
