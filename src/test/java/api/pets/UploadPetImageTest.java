package api.pets;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import petstore.models.ApiResponse;
import petstore.models.pets.Pet;
import petstore.services.PetService;
import petstore.util.FileProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getPet;

@DisplayName("Тесты на загрузку изображения питомца")
public class UploadPetImageTest extends BaseTest {

    private final PetService petService = new PetService();
    private long id;
    private static final String IMG_NAME = "taxa.jpeg";
    private static final String TXT_NAME = "S0305054807000986.txt";
    private static final long IMG_SIZE = FileProvider.getFile(IMG_NAME).length();

    @BeforeAll
    public void prepare() {
        Pet pet = petService.createPet(getPet("Nach", ""),
                SC_OK);
        id = pet.getId();
    }

    @AfterAll
    public void clear() {
        petService.deletePet(id, SC_OK);
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Загрузка валидного изображения питомца")
    public void uploadValidImage() {
        String metadata = "formData";
        String message = "additionalMetadata: " + metadata +"\nFile uploaded to ./" +
                IMG_NAME + ", " + IMG_SIZE + " bytes";
        Response response = petService.uploadImage(id, metadata, IMG_NAME, SC_OK);
        ApiResponse body = response.as(ApiResponse.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG),
                () -> assertEquals(SC_OK, body.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, body.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(message, body.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Загрузка валидного изображения питомца без опционального параметра")
    public void uploadValidImageWithoutAdditionalMetadata() {
        String message = "additionalMetadata: null\nFile uploaded to ./" +
            IMG_NAME + ", " + IMG_SIZE + " bytes";
        Response response = petService.uploadImage(id, IMG_NAME, SC_OK);
        ApiResponse body = response.as(ApiResponse.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG),
                () -> assertEquals(SC_OK, body.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, body.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(message, body.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Загрузка текстового документа для аватара питомца")
    public void uploadNotImage() {
        long fileSize = FileProvider.getFile(TXT_NAME).length();
        String message = "additionalMetadata: null\nFile uploaded to ./" +
                TXT_NAME + ", " + fileSize + " bytes";
        Response response = petService.uploadImage(id, TXT_NAME, SC_OK);
        ApiResponse body = response.as(ApiResponse.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG),
                () -> assertEquals(SC_OK, body.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, body.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(message, body.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Загрузка изображения для несуществующего питомца")
    public void uploadImageForUnexistedPet() {
        String metadata = "formData";
        String message = "additionalMetadata: " + metadata +"\nFile uploaded to ./" +
                IMG_NAME + ", " + IMG_SIZE + " bytes";
        Response response = petService.uploadImage(0L, metadata, IMG_NAME, SC_NOT_FOUND);
        ApiResponse body = response.as(ApiResponse.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG),
                () -> assertEquals(SC_NOT_FOUND, body.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, body.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(message, body.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Отправка запроса без файла")
    public void uploadWithoutFile() {
        Response response = petService.uploadImage(id, SC_BAD_REQUEST);
        ApiResponse body = response.as(ApiResponse.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        assertAll(
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG),
                () -> assertEquals(SC_BAD_REQUEST, body.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, body.getType(), RESPONSE_TYPE_WRONG),
                () -> assertTrue(body.getMessage().contains("org.jvnet.mimepull.MIMEParsingException: Missing start boundary"),
                        RESPONSE_MESSAGE_WRONG)
        );
    }
}
