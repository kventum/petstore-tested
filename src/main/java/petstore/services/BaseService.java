package petstore.services;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static petstore.constants.Endpoints.BASE_URL;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public abstract class BaseService {

    protected abstract String getBasePath();

    public RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_URL)
                    .setBasePath(getBasePath())
                    .setContentType(ContentType.JSON)
                    .addFilter(new AllureRestAssured())
                    .addHeader("api_key", "special-key")
                    .build();

    public static ResponseSpecification responseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification responseSpec(int statusCode, ContentType contentType) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType(contentType)
                .expectResponseTime(lessThanOrEqualTo(3000L))
                .log(LogDetail.ALL)
                .build();
    }

    public Response get(String endpoint) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .get(endpoint);
    }

    public Response get(String endpoint, Map<String, String> queries) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .queryParams(queries)
                .get(endpoint);
    }

    public Response get(String endpoint, Map<String, String> queries, Map<String, Object> headers) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .queryParams(queries)
                .headers(headers)
                .get(endpoint);
    }

    public Response get(String endpoint, String param, Object value) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .pathParam(param, value)
                .get(endpoint);
    }

    public Response post(String endpoint, Object requestBody) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .body(requestBody)
                .post(endpoint);
    }

    public Response put(String endpoint, Object requestBody) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .body(requestBody)
                .put(endpoint);
    }

    public Response put(String endpoint, String param, Object value, Object requestBody) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .pathParam(param, value)
                .body(requestBody)
                .put(endpoint);
    }

    public Response delete(String endpoint, String param, Object value) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .pathParam(param, value)
                .delete(endpoint);
    }
}
