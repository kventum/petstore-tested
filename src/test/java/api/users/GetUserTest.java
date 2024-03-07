package api.users;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import petstore.models.ApiResponse;
import petstore.models.User;
import petstore.services.UserService;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Endpoints.EMPTY;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getUser;

@DisplayName("Тесты на получение пользователя")
public class GetUserTest extends BaseTest {

    private final UserService userService = new UserService();
    String validUsername;
    String deletedUsername;
    User validUser;
    User deletedUser;

    @BeforeAll
    protected void prepare() {
        validUser = getUser(4, 0);
        userService.createUser(EMPTY, validUser, SC_OK);
        validUsername = validUser.getUsername();
        deletedUser = getUser(5, 2);
        userService.createUser(EMPTY, deletedUser, SC_OK);
        deletedUsername = deletedUser.getUsername();
    }

    @AfterAll
    protected void clear() {
        userService.deleteUser(validUsername, SC_OK);
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Получение пользователя по валидному username")
    public void getValidUser() {
        User response = userService.getUser(validUsername, SC_OK).as(User.class);

        assertAll(
                () -> assertEquals(validUser.getId(), response.getId(), USER_ID_WRONG),
                () -> assertEquals(validUsername, response.getUsername(), USERNAME_WRONG),
                () -> assertEquals(validUser.getFirstName(), response.getFirstName(), USER_FIRST_NAME_WRONG),
                () -> assertEquals(validUser.getLastName(), response.getLastName(), USER_LAST_NAME_WRONG),
                () -> assertEquals(validUser.getEmail(), response.getEmail(), USER_EMAIL_WRONG),
                () -> assertEquals(validUser.getPassword(), response.getPassword(), USER_PASSWORD_WRONG),
                () -> assertEquals(validUser.getPhone(), response.getPhone(), USER_PHONE_WRONG),
                () -> assertEquals(validUser.getUserStatus(), response.getUserStatus(), USER_STATUS_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Получение пользователя по пустому username")
    public void getUserByEmptyUsername() {
        userService.getUser("", SC_METHOD_NOT_ALLOWED, ContentType.XML);
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Получение удаленного ранее пользователя")
    public void getDeletedUser() {
        userService.deleteUser(deletedUsername, SC_OK);

        ApiResponse response = userService.getUser(deletedUsername, SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(1, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(ERROR_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals("User not found", response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }
}
