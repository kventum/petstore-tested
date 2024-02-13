package petstore.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import petstore.models.Order;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static petstore.constants.Endpoints.*;

public class StoreService extends BaseService{

    @Override
    protected String getBasePath() {
        return STORE;
    }

    public Order createOrder(Object request, int statusCode) {
        return post(ORDER, request)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().as(Order.class);
    }

    public Response deleteOrder(Object id, int statusCode) {
        return delete(ORDER_BY_ID, "id", id)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response getOrder(Object id, int statusCode) {
        return get(ORDER_BY_ID, "id", id)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response getInventory(int statusCode, String path) {
        return get(INVENTORY)
                .then()
                .body(matchesJsonSchemaInClasspath(path))
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }
}
