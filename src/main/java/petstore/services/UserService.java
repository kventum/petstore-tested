package petstore.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import petstore.models.User;

import java.util.List;
import java.util.Map;

import static petstore.constants.Endpoints.*;

public class UserService extends BaseService{

    @Override
    protected String getBasePath() {
        return USER;
    }

    public Response createListUsers (String endpoint, List<User> requestBody, int statusCode) {
        return post(endpoint, requestBody)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response createUser (String endpoint, User requestBody, int statusCode) {
        return post(endpoint, requestBody)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response deleteUser(String username, int statusCode) {
        return delete(USER_BY_NAME, "username", username)
                .then()
                .spec(responseSpec(statusCode))
                .extract().response();
    }

    public Response deleteUser(String username, int statusCode, ContentType contentType) {
        return delete(USER_BY_NAME, "username", username)
                .then()
                .spec(responseSpec(statusCode, contentType))
                .extract().response();
    }

    public Response getUser(String username, int statusCode) {
        return get(USER_BY_NAME, "username", username)
                .then()
                .spec(responseSpec(statusCode))
                .extract().response();
    }

    public Response getUser(String username, int statusCode, ContentType contentType) {
        return get(USER_BY_NAME, "username", username)
                .then()
                .spec(responseSpec(statusCode, contentType))
                .extract().response();
    }

    public Response loginUser(String username, String password, int statusCode) {
        return get(LOGIN, Map.of("username", username, "password", password))
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response loginUser(String username, String password, String expires, int limit, int statusCode) {
        return get(LOGIN, Map.of("username", username, "password", password),
                Map.of("X-Expires-After", expires, "X-Rate-Limit", limit))
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response updateUser(User requestBody, String userName, int statusCode) {
        return put(USER_BY_NAME, "username", userName, requestBody)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }
}
