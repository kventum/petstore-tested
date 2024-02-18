package api.users;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import petstore.models.ApiResponse;
import petstore.models.User;
import petstore.services.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Endpoints.EMPTY;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getUser;

@DisplayName("Тесты на создание пользователя")
public class CreateUserTest extends BaseTest {

    private final UserService userService = new UserService();
    private final List<User> userList = new ArrayList<>();

    @Override
    protected void prepare() {}

    @AfterAll
    protected void clear() {
        userList.forEach(user -> userService.deleteUser(user.getUsername(), SC_OK));
    }

    @ParameterizedTest
    @CsvFileSource(delimiter = ';', numLinesToSkip = 1, resources = "/users/createUserWithAllFields.csv")
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Создание валидного пользователя с разными данными")
    public void createValidUser(String username, String firstName, String lastName, String email, String password,
                                String phone, int status) {
        User requestBody = getUser(username, firstName, lastName, email, password, phone, status);
        ApiResponse response = userService.createUser(EMPTY, requestBody, SC_OK).as(ApiResponse.class);

        userList.add(requestBody);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(requestBody.getId().toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(POSITIVE)
    @DisplayName("Создание пользователя без id")
    public void createUserWithoutId() {
        User requestBody = User.builder()
                .username("Laos")
                .firstName("Laos")
                .lastName("Cambodia")
                .email("lao@s")
                .password("1234")
                .phone("+856855323101")
                .userStatus(1)
                .build();
        ApiResponse response = userService.createUser(EMPTY, requestBody, SC_OK).as(ApiResponse.class);

        userList.add(requestBody);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertNotNull(response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователя без username")
    public void createUserWithoutUsername() {
        User requestBody = User.builder()
                .id((long) (Math.random() * 10000))
                .firstName("Ronny")
                .lastName("Montana")
                .email("ronny@montana.us")
                .password("abcdef")
                .phone("+13135131511")
                .userStatus(1)
                .build();
        ApiResponse response = userService.createUser(EMPTY, requestBody, SC_OK).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertNotNull(response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Создание пользователя без пароля")
    public void createUserWithoutPassword() {
        User requestBody = User.builder()
                .id((long) (Math.random() * 10000))
                .username("carrot")
                .firstName("car")
                .lastName("bunny")
                .email("carrot@mob.us")
                .phone("+13130000000")
                .userStatus(0)
                .build();
        ApiResponse response = userService.createUser(EMPTY, requestBody, SC_OK).as(ApiResponse.class);

        userList.add(requestBody);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(requestBody.getId().toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Создание пользователя только со статусом")
    public void createUserOnlyWithStatus() {
        User requestBody = User.builder()
                .userStatus(0)
                .build();
        ApiResponse response = userService.createUser(EMPTY, requestBody, SC_OK).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("0", response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }
}
