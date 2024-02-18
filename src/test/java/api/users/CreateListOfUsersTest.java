package api.users;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import petstore.models.ApiResponse;
import petstore.models.User;
import petstore.services.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Endpoints.CREATE_WITH_ARRAY;
import static petstore.constants.Endpoints.CREATE_WITH_LIST;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getUser;
import static petstore.util.DataGenerator.getUsers;

@DisplayName("Создание нескольких пользователей (через эндпоинты " + CREATE_WITH_ARRAY + " и " + CREATE_WITH_LIST + ")")
public class CreateListOfUsersTest extends BaseTest {

    private final UserService userService = new UserService();
    private final List<User> userList = new ArrayList<>();

    @Override
    protected void prepare() {}

    @AfterAll
    protected void clear() {
        userList.forEach(user -> userService.deleteUser(user.getUsername(), SC_OK));
    }

    @ParameterizedTest
    @ValueSource(strings = {CREATE_WITH_ARRAY, CREATE_WITH_LIST})
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание одного пользователя из массива через {0}")
    public void createArrayOfOneUser(String endpoint) {
        List<User> request = List.of(getUser(6, 0));
        ApiResponse response = userService.createListUsers(endpoint, request, SC_OK).as(ApiResponse.class);

        userList.addAll(request);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("ok", response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {CREATE_WITH_ARRAY, CREATE_WITH_LIST})
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание нескольких пользователей из массива через {0}")
    public void createArrayOfUsers(String endpoint) {
        List<User> request = getUsers(3);
        ApiResponse response = userService.createListUsers(endpoint, request, SC_OK).as(ApiResponse.class);

        userList.addAll(request);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("ok", response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {CREATE_WITH_ARRAY, CREATE_WITH_LIST})
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Создание пользователей пустым массивом")
    public void createUsersByEmptyArray(String endpoint) {
        List<User> request = new ArrayList<>();
        ApiResponse response = userService.createListUsers(endpoint, request, SC_OK).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("ok", response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {CREATE_WITH_ARRAY, CREATE_WITH_LIST})
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Создание одного пользователя объектом без массива")
    public void createOneUserWithoutArray(String endpoint) {
        User request = getUser(6, 0);
        ApiResponse response = userService.createUser(endpoint, request, SC_INTERNAL_SERVER_ERROR)
                .as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_INTERNAL_SERVER_ERROR, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(SMTH_BAD, response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }
}
