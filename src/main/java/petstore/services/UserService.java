package petstore.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import petstore.models.User;

import java.util.List;

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
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }
}
