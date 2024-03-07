package api.users;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import petstore.models.ApiResponse;
import petstore.models.User;
import petstore.services.UserService;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Endpoints.EMPTY;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getUser;

@DisplayName("Тесты на удаление пользователя")
public class DeleteUserTest extends BaseTest {

    private final UserService userService = new UserService();
    private User validUser;
    private User deletedUser;

    @BeforeAll
    protected void prepare() {
        validUser = getUser(4, 0);
        deletedUser = getUser(6, 1);
        userService.createUser(EMPTY, validUser, SC_OK);
        userService.createUser(EMPTY, deletedUser, SC_OK);
        userService.deleteUser(deletedUser.getUsername(), SC_OK);
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаление валидного пользователя")
    public void deleteValidUser() {
        ApiResponse response = userService.deleteUser(validUser.getUsername(), SC_OK, ContentType.JSON).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertNotNull(response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Удаление несуществующего пользователя")
    public void deleteNonexistentUser() {
        Response response = userService.deleteUser("non_existent_user", SC_NOT_FOUND);

        assertEquals("0", response.getHeader("Content-Length"));
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Удаление ранее удаленного пользователя")
    public void deleteDeletedUser() {
        Response response = userService.deleteUser(deletedUser.getUsername(), SC_NOT_FOUND);

        assertEquals("0", response.getHeader("Content-Length"));
    }
}
