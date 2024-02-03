package api.pets;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import petstore.models.pets.Pet;
import petstore.models.pets.PetStatus;
import petstore.services.PetService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;

@DisplayName("Тесты на поиск питомцев по статусу")
public class FindPetsTest {

    private final PetService petService = new PetService();

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Проверка получения питомцев без статуса")
    public void findPetWithoutStatusFilter() {
        Response response = petService.getPets(SC_OK);
        List<Pet> pets = response.jsonPath().getList(".", Pet.class);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        Assertions.assertAll(
                () -> assertTrue(pets.isEmpty(), LIST_NOT_NULL),
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG)
        );
    }

    @ParameterizedTest
    @EnumSource(PetStatus.class)
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка фильтрации питомцев по одному статусу")
    public void findPetByOneStatus(PetStatus status) {
        List<Pet> pets = petService.getPets(status.name(), SC_OK);

        pets.forEach(pet -> Assertions.assertAll(
                    () -> assertEquals(status, pet.getStatus(), PET_STATUS_WRONG),
                    () -> assertNotNull(pet.getId(), PET_ID_NULL),
                    () -> assertNotNull(pet.getName(), PET_NAME_NULL),
                    () -> assertTrue(pet.getPhotoUrls().length >= 0, PET_PHOTO_URL_NULL))
        );
    }

    @ParameterizedTest
    @MethodSource("statusCombinations")
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Проверка фильтрации питомцев сразу по нескольким статусам")
    public void findPetBySomeStatuses(String statuses) {
        List<Pet> pets = petService.getPets(statuses, SC_OK);

        pets.forEach(pet -> Assertions.assertAll(
                () -> assertTrue(statuses.contains(pet.getStatus().name()), PET_STATUS_WRONG),
                () -> assertNotNull(pet.getId(), PET_ID_NULL),
                () -> assertNotNull(pet.getName(), PET_NAME_NULL),
                () -> assertTrue(pet.getPhotoUrls().length >= 0, PET_PHOTO_URL_NULL))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "notExisted", "1"})
    @Tag(NEGATIVE)
    @DisplayName("Проверка фильтрации питомцев по несуществующим статусам")
    public void findPetByNotExistedStatus(String status) {
        Response response = petService.getPets(SC_OK);
        List<Pet> pets = petService.getPets(status, SC_OK);
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        Assertions.assertAll(
                () -> assertTrue(pets.isEmpty(), LIST_NOT_NULL),
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG)
        );
    }

    private static Stream<String> statusCombinations() {
        return Stream.of(
                PetStatus.available + "," + PetStatus.pending,
                PetStatus.available + "," + PetStatus.sold,
                PetStatus.pending + "," + PetStatus.sold,
                PetStatus.available + "," + PetStatus.pending + "," + PetStatus.sold
        );
    }
}
