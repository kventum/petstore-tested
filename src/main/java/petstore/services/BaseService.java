package petstore.services;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static petstore.constants.Endpoints.BASE_URL;

public abstract class BaseService {

    protected abstract String getBasePath();

    public final RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_URL)
                    .setBasePath(getBasePath())
                    .setContentType(ContentType.JSON)
                    .addHeader("api_key", "special-key")
                    .build();
}
