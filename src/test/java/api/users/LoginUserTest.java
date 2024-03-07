package api.users;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import petstore.models.ApiResponse;
import petstore.models.User;
import petstore.services.UserService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Endpoints.EMPTY;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getUsers;

public class LoginUserTest extends BaseTest {

    private static final String LOGGED_IN_MSG = "logged in user session:";
    private final UserService userService = new UserService();
    private final String regexSession = "\\d{13}";
    private List<User> userList = new ArrayList<>();
    private User user1;
    private User user2;

    @BeforeAll
    protected void prepare() {
        userList = getUsers(5);
        userList.forEach(user -> userService.createUser(EMPTY, user, SC_OK));
        user1 = userList.get(0);
        user2 = userList.get(1);
    }

    @AfterAll
    protected void clear() {
        userList.forEach(user -> userService.deleteUser(user.getUsername(), SC_OK));
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Логин корректного пользователя")
    public void loginCorrectUser() {
        ApiResponse response = userService.loginUser(user1.getUsername(), user1.getPassword(), SC_OK).as(ApiResponse.class);
        Matcher matcher = Pattern.compile(regexSession).matcher(response.getMessage());

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertTrue(response.getMessage().startsWith(LOGGED_IN_MSG), RESPONSE_MESSAGE_WRONG),
                () -> assertTrue(matcher.find(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин валидного пользователя с корректными заголовками")
    public void loginUserWithCorrectHeaders() {
        int rateLimit = 2;
        OffsetDateTime expireDateTime = OffsetDateTime.now(ZoneOffset.UTC).withNano(0).plusHours(2);

        Response response = userService.loginUser(
                user2.getUsername(), user2.getPassword(), expireDateTime.toString(), rateLimit, SC_OK);
        ApiResponse responseBody = response.as(ApiResponse.class);
        Matcher matcher = Pattern.compile(regexSession).matcher(responseBody.getMessage());

        LocalDateTime expiresAfter = LocalDateTime.parse(response.getHeader("X-Expires-After"),
                DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss 'UTC' yyyy", Locale.US));

        assertAll(
                () -> assertEquals(SC_OK, responseBody.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, responseBody.getType(), RESPONSE_TYPE_WRONG),
                () -> assertTrue(responseBody.getMessage().startsWith(LOGGED_IN_MSG), RESPONSE_MESSAGE_WRONG),
                () -> assertTrue(matcher.find(), RESPONSE_MESSAGE_WRONG),
                () -> assertEquals(Integer.toString(rateLimit), response.getHeader("X-Rate-Limit"), RATE_LIMIT_WRONG),
                () -> assertEquals(expireDateTime.toLocalDateTime(), expiresAfter, EXPIRES_AFTER_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин пользователя с неправильным паролем")
    public void loginUserWithWrongPassword() {
        ApiResponse response = userService.loginUser(userList.get(2).getUsername(), "1234", SC_BAD_REQUEST)
                .as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_BAD_REQUEST, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(ERROR_TYPE, response.getType(), RESPONSE_TYPE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Логин пользователя с пустым паролем")
    public void loginUserWithEmptyPassword() {
        ApiResponse response = userService.loginUser(userList.get(3).getUsername(), "", SC_BAD_REQUEST)
                .as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_BAD_REQUEST, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(ERROR_TYPE, response.getType(), RESPONSE_TYPE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Логин удаленного ранее пользователя")
    public void loginDeletedUser() {
        User user = userList.get(4);
        String username = user.getUsername();
        String password = user.getPassword();
        userService.deleteUser(username, SC_OK);
        userList.remove(user);

        ApiResponse response = userService.loginUser(username, password, SC_BAD_REQUEST).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_BAD_REQUEST, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG)
        );
    }
}
