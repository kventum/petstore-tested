package api.pets;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import petstore.models.pets.Category;
import petstore.models.pets.Pet;
import petstore.models.pets.PetStatus;
import petstore.services.PetService;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_METHOD_NOT_ALLOWED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;

@DisplayName("Тесты на создание питомца")
public class CreatePetTest extends BaseTest {

    private final PetService petService = new PetService();
    private final List<Pet> petList = new ArrayList<>();

    public void prepare() {}

    @AfterAll
    public void clear() {
        petList.forEach(pet -> petService.deletePet(pet.getId(), SC_OK));
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
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

        Pet response = petService.createPet(request, SC_OK);

        petList.add(response);

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

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Создание питомца только с обязательными полями")
    public void createPetWithRequiredFields() {
        String petName = "Requi";
        String photoUrl = "https://www.mybestfrienddogcare.co.uk/wp-content/uploads/2020/09/Doggy-Day-Care-in-Andover-.jpg";
        Pet request = Pet.builder()
                .name(petName)
                .photoUrls(new String[]{photoUrl})
                .build();

        Pet response = petService.createPet(request, SC_OK);

        petList.add(response);

        assertAll(
                () -> assertEquals(petName, response.getName(), PET_NAME_WRONG),
                () -> assertEquals(photoUrl, response.getPhotoUrls()[0], PET_PHOTO_URL_WRONG),
                () -> assertNotNull(response.getId(), PET_ID_NULL),
                () -> assertNull(response.getCategory(), "Pet has category"),
                () -> assertNull(response.getTags(), "Pet has tags"),
                () -> assertNull(response.getStatus(), "Pet has status")
        );
    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание питомца без имени")
    public void createPetWithoutName() {
        String photoUrl = "https://www.mybestfrienddogcare.co.uk/wp-content/uploads/2020/09/Doggy-Day-Care-in-Andover-.jpg";
        Pet request = Pet.builder()
                .photoUrls(new String[]{photoUrl})
                .build();

        Pet response = petService.createPet(request, SC_METHOD_NOT_ALLOWED);

        assertAll(
                () -> assertNull(response.getName(), "Pet name is not null"),
                () -> assertEquals(photoUrl, response.getPhotoUrls()[0], PET_PHOTO_URL_WRONG),
                () -> assertNotNull(response.getId(), PET_ID_NULL),
                () -> assertNull(response.getCategory(), "Pet has category"),
                () -> assertNull(response.getTags(), "Pet has tags"),
                () -> assertNull(response.getStatus(), "Pet has status")
        );
    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание питомца без фото")
    public void createPetWithoutPhotoUrls() {
        String petName = "Garfield";
        Pet request = Pet.builder()
                .name(petName)
                .build();

        Pet response = petService.createPet(request, SC_METHOD_NOT_ALLOWED);

        assertAll(
                () -> assertEquals(petName, response.getName(), PET_NAME_WRONG),
                () -> assertNull(response.getPhotoUrls(), "Pet has photo urls"),
                () -> assertNotNull(response.getId(), PET_ID_NULL),
                () -> assertNull(response.getCategory(), "Pet has category"),
                () -> assertNull(response.getTags(), "Pet has tags"),
                () -> assertNull(response.getStatus(), "Pet has status")
        );
    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Создание питомца методом GET")
    public void createPetByGetRequest() {
        String petName = "Ceprany";
        String photoUrl = "https://www.mybestfrienddogcare.co.uk/wp-content/uploads/2020/09/Doggy-Day-Care-in-Andover-.jpg";
        Pet request = Pet.builder()
                .name(petName)
                .photoUrls(new String[]{photoUrl})
                .build();

        given()
                .spec(petService.REQUEST_SPECIFICATION)
                .body(request)
                .when().log().all()
                .get()
                .then().log().all()
                .statusCode(SC_METHOD_NOT_ALLOWED);
    }
}
