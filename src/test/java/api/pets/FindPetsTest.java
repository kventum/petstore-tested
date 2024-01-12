package api.pets;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Others.NEGATIVE;
import static petstore.constants.Others.POSITIVE;
import static petstore.steps.PetSteps.getPets;
import static petstore.steps.PetSteps.getPetsResponse;

public class FindPetsTest {

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Проверка получения питомцев без статуса")
    public void findPetWithoutStatusFilter() {
        Response response = getPetsResponse();
        List<Pet> pets = getPets();
        LocalDateTime responseTime = LocalDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);

        Assertions.assertAll(
                () -> assertTrue(pets.isEmpty(), LIST_NOT_NULL),
                () -> assertTrue(LocalDateTime.now().isAfter(responseTime), RESPONSE_TIME_WRONG)
        );
    }

    @ParameterizedTest
    @EnumSource(PetStatus.class)
    @Tag(POSITIVE)
    @DisplayName("Проверка фильтрации питомцев по одному статусу")
    public void findPetByOneStatus(PetStatus status) {
        List<Pet> pets = getPets(status.name());

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
    @DisplayName("Проверка фильтрации питомцев сразу по нескольким статусам")
    public void findPetBySomeStatuses(String statuses) {
        List<Pet> pets = getPets(statuses);

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
        Response response = getPetsResponse();
        List<Pet> pets = getPets(status);
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
