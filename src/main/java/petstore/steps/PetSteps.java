package petstore.steps;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import petstore.models.pets.Pet;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static petstore.constants.Endpoints.*;

public class PetSteps {
    private static final RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_URL)
                    .setBasePath(PET)
                    .setContentType(ContentType.JSON)
                    .build();

    public static Pet createPet(Pet request) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(request)
                .when().log().all()
                .post()
                .then().log().all()
                .statusCode(SC_OK)
                .extract().as(Pet.class);
    }

    public static Response getPetsResponse() {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .get(FIND_BY_STATUS_EP)
                .then().log().all()
                .statusCode(SC_OK)
                .extract().response();
    }

    public static List<Pet> getPets() {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .when().log().all()
                .get(FIND_BY_STATUS_EP)
                .then().log().all()
                .statusCode(SC_OK)
                .extract().jsonPath().getList(".", Pet.class);
    }

    public static List<Pet> getPets(String status) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .when().log().all()
                .queryParams(Map.of("status", status))
                .get(FIND_BY_STATUS_EP)
                .then().log().all()
                .statusCode(SC_OK)
                .extract().jsonPath().getList(".", Pet.class);
    }
}
