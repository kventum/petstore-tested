package api.pets;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;
import petstore.models.pets.Pet;
import petstore.models.pets.PetStatus;
import petstore.services.PetService;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;
import static petstore.util.DataGenerator.getPet;

@DisplayName("Тесты на обновление существующего питомца")
public class UpdatePetTest extends BaseTest {

    PetService petService = new PetService();
    private long id;
    private long categoryId;
    private long tagId;

    @BeforeAll
    public void prepare() {
        Pet pet = petService.createPet(getPet("Nach", "kots", "", "kotik", PetStatus.pending),
                SC_OK);
        id = pet.getId();
        categoryId = pet.getCategory().getId();
        tagId = pet.getTags().get(0).getId();
    }

    @AfterAll
    public void clear() {
        petService.deletePet(id, SC_OK);
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Обновление всех полей питомца")
    public void updateAllPetFields() {
        String name = "Nachos";
        String categoryName = "cats";
        String url = "https://www.animalfriends.co.uk/siteassets/media/images/article-images/cat-articles/51_afi_article1_the-secret-language-of-cats.png";
        String tag = "cat";

        Pet response = petService.updatePet(getPet(id, name, categoryId, categoryName, url, tagId, tag, PetStatus.available),
                SC_OK);

        assertAll(
                () -> assertEquals(id, response.getId()),
                () -> assertEquals(name, response.getName(), PET_NAME_WRONG),
                () -> assertEquals(categoryId, response.getCategory().getId()),
                () -> assertEquals(categoryName, response.getCategory().getName(), PET_CATEGORY_NAME_WRONG),
                () -> assertEquals(url, response.getPhotoUrls()[0], PET_PHOTO_URL_WRONG),
                () -> assertEquals(tagId, response.getTags().get(0).getId()),
                () -> assertEquals(tag, response.getTags().get(0).getName()),
                () -> assertEquals(PetStatus.available, response.getStatus(), PET_STATUS_WRONG)
        );
    }

    @Test
    @Tag(POSITIVE)
    @DisplayName("Обновление обязательных полей и зануление остальных")
    public void updateRequiredAndNullOtherFields() {
        String name = "Nachos";
        String url = "https://www.animalfriends.co.uk/siteassets/media/images/article-images/cat-articles/51_afi_article1_the-secret-language-of-cats.png";

        Pet response = petService.updatePet(getPet(id, name, null, null, url, null, null, null),
                SC_OK);

        assertAll(
                () -> assertEquals(id, response.getId()),
                () -> assertEquals(name, response.getName(), PET_NAME_WRONG),
                () -> assertEquals(0, response.getCategory().getId()),
                () -> assertNull(response.getCategory().getName()),
                () -> assertEquals(url, response.getPhotoUrls()[0], PET_PHOTO_URL_WRONG),
                () -> assertEquals(0, response.getTags().get(0).getId()),
                () -> assertNull(response.getTags().get(0).getName()),
                () -> assertNull(response.getStatus(), PET_STATUS_WRONG)
        );
    }

    @Test
    @Tag(POSITIVE)
    @DisplayName("Обновление только обязательных полей, остальные поля не используются")
    public void updateRequiredFields() {
        String name = "Nachos";
        String url = "https://www.animalfriends.co.uk/siteassets/media/images/article-images/cat-articles/51_afi_article1_the-secret-language-of-cats.png";

        Pet response = petService.updatePet(getPet(id, name,  url), SC_OK);

        assertAll(
                () -> assertEquals(id, response.getId()),
                () -> assertEquals(name, response.getName(), PET_NAME_WRONG),
                () -> assertEquals(url, response.getPhotoUrls()[0], PET_PHOTO_URL_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Обновление питомца без передачи полей")
    public void updateWithoutFields() {
        Pet response = petService.updatePet(getPet(id), SC_OK);

        assertAll(
                () -> assertEquals(id, response.getId())
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Обновление несуществующего питомца")
    public void updateUnexistedPet() {
        petService.updatePet(getPet(0L, "new_name", "-"), SC_NOT_FOUND);
    }
}
