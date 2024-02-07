package petstore.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import petstore.models.Order;

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
                .spec(responseSpec(statusCode))
                .extract().response();
    }
}
