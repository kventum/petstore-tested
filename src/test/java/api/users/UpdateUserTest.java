package api.users;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import petstore.models.ApiResponse;
import petstore.models.User;
import petstore.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static petstore.constants.AssertMessages.*;
import static petstore.constants.Endpoints.EMPTY;
import static petstore.constants.Others.*;
import static petstore.util.DataGenerator.getUser;
import static petstore.util.DataGenerator.getUsers;

@DisplayName("Тесты на обновление пользователей")
public class UpdateUserTest extends BaseTest {

    private static final String DATA_FILE_NAME = "/users/createUserWithAllFields.csv";
    private final UserService userService = new UserService();
    private List<User> validUserList = new ArrayList<>();
    private final List<User> usersForDeleting = new ArrayList<>();
    private User notValidUser;
    private User deletedUser;

    @BeforeAll
    protected void prepare() {
        validUserList = getUsers(3);
        validUserList.forEach(user -> userService.createUser(EMPTY, user, SC_OK));
        usersForDeleting.addAll(validUserList);
        notValidUser = getUser(8, 1);
        userService.createUser(EMPTY, notValidUser, SC_OK);
        usersForDeleting.add(notValidUser);
        deletedUser = getUser(6, 1);
        userService.createUser(EMPTY, deletedUser, SC_OK);
        userService.deleteUser(deletedUser.getUsername(), SC_OK);
    }

    @AfterAll
    protected void clear() {
        usersForDeleting.forEach(user -> userService.deleteUser(user.getUsername(), SC_OK));
    }

    @ParameterizedTest
    @CsvFileSource(delimiter = ';', numLinesToSkip = 1, resources = DATA_FILE_NAME)
    @Tag(NEGATIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Обновление всех полей пользователя")
    public void updateAllFields(String username, String firstName, String lastName, String email, String password,
                                String phone, int status) {
        User request = getUser(username, firstName, lastName, email, password, phone, status);
        ApiResponse response = userService.updateUser(request, request.getUsername(), SC_OK).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(request.getId().toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );

        User expUser = userService.getUser(request.getUsername(), SC_OK).as(User.class);
        assertAll(
                () -> assertEquals(request.getId(), expUser.getId(), USER_ID_WRONG),
                () -> assertEquals(username, expUser.getUsername(), USERNAME_WRONG),
                () -> assertEquals(firstName, expUser.getFirstName(), USER_FIRST_NAME_WRONG),
                () -> assertEquals(lastName, expUser.getLastName(), USER_LAST_NAME_WRONG),
                () -> assertEquals(email, expUser.getEmail(), USER_EMAIL_WRONG),
                () -> assertEquals(password, expUser.getPassword(), USER_PASSWORD_WRONG),
                () -> assertEquals(phone, expUser.getPhone(), USER_PHONE_WRONG),
                () -> assertEquals(status, expUser.getUserStatus(), USER_STATUS_WRONG)
        );
    }

    @ParameterizedTest
    @CsvFileSource(delimiter = ';', numLinesToSkip = 1, resources = DATA_FILE_NAME)
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Обновление всех полей пользователя кроме username")
    public void updateAllFieldsExceptOfUsername(String username, String firstName, String lastName, String email, String password,
                                                String phone, int status, TestInfo testInfo) {
        User request = getUser(username, firstName, lastName, email, password, phone, status);
        request.setUsername(
                validUserList.get(getTestMethodIndex(testInfo)).getUsername());     // возвращаем изначальное значение username
        ApiResponse response = userService.updateUser(request, request.getUsername(), SC_OK).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_OK, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(request.getId().toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );

        User expUser = userService.getUser(request.getUsername(), SC_OK).as(User.class);
        assertAll(
                () -> assertEquals(request.getId(), expUser.getId(), USER_ID_WRONG),
                () -> assertEquals(request.getUsername(), expUser.getUsername(), USERNAME_WRONG),
                () -> assertEquals(firstName, expUser.getFirstName(), USER_FIRST_NAME_WRONG),
                () -> assertEquals(lastName, expUser.getLastName(), USER_LAST_NAME_WRONG),
                () -> assertEquals(email, expUser.getEmail(), USER_EMAIL_WRONG),
                () -> assertEquals(password, expUser.getPassword(), USER_PASSWORD_WRONG),
                () -> assertEquals(phone, expUser.getPhone(), USER_PHONE_WRONG),
                () -> assertEquals(status, expUser.getUserStatus(), USER_STATUS_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Обновление всех полей пользователя невалидными значениями")
    public void updateAllFieldsInvalid() {
        User request = getUser("-", "1@", "089", "22sd", "1", "jdbc", -10);
        ApiResponse response = userService.updateUser(request, notValidUser.getUsername(), SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(request.getId().toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    @Test
    @Tag(NEGATIVE)
    @DisplayName("Обновление несуществующего пользователя")
    public void updateNonexistentUser() {
        User request = getUser(deletedUser.getUsername(), "Van", "Helsing", "van@hel.hl", "1234", "+3542120212", 2);
        ApiResponse response = userService.updateUser(request, notValidUser.getUsername(), SC_NOT_FOUND).as(ApiResponse.class);

        assertAll(
                () -> assertEquals(SC_NOT_FOUND, response.getCode(), RESPONSE_CODE_WRONG),
                () -> assertEquals(UNKNOWN_TYPE, response.getType(), RESPONSE_TYPE_WRONG),
                () -> assertEquals(request.getId().toString(), response.getMessage(), RESPONSE_MESSAGE_WRONG)
        );
    }

    /**
     * Метод, возвращающий индекс текущего выполняющегося теста в параметризованных тестах
     */
    private int getTestMethodIndex(TestInfo testInfo) {
        int index;
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(testInfo.getDisplayName());
        if (matcher.find()) {
            index = Integer.parseInt(matcher.group(1));
        } else throw new NoSuchElementException();
        return index - 1;
    }
}
