package petstore.services;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.apache.http.HttpStatus.SC_OK;
import static petstore.constants.Endpoints.BASE_URL;

public abstract class BaseService {

    protected abstract String getBasePath();

    public RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_URL)
                    .setBasePath(getBasePath())
                    .setContentType(ContentType.JSON)
                    .addHeader("api_key", "special-key")
                    .build();

    public static ResponseSpecification responseSpecOk = new ResponseSpecBuilder()
            .expectStatusCode(SC_OK)
            .log(LogDetail.ALL)
            .build();
}
