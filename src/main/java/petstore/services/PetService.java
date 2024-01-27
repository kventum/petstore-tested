package petstore.services;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import petstore.models.pets.Pet;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static petstore.constants.Endpoints.*;

public class PetService extends BaseService {

    @Override
    protected String getBasePath() {
        return PET;
    }

    public Pet createPet(Pet request, int statusCode) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(request)
                .when().log().all()
                .post()
                .then().log().all()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(Pet.class);
    }

    public Response getPetsResponse() {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .log().all()
                .get(FIND_BY_STATUS_EP)
                .then().log().all()
                .statusCode(SC_OK)
                .header("Content-Type", ContentType.JSON.toString())
                .extract().response();
    }

    public List<Pet> getPets() {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .when().log().all()
                .get(FIND_BY_STATUS_EP)
                .then().log().all()
                .statusCode(SC_OK)
                .extract().jsonPath().getList(".", Pet.class);
    }

    public List<Pet> getPets(String status) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .when().log().all()
                .queryParams(Map.of("status", status))
                .get(FIND_BY_STATUS_EP)
                .then().log().all()
                .statusCode(SC_OK)
                .extract().jsonPath().getList(".", Pet.class);
    }

    public ExtractableResponse<Response> getPetById(Object id, int statusCode) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .pathParam("id", id)
                .when().log().all()
                .get(PET_BY_ID)
                .then().log().all()
                .statusCode(statusCode)
                .extract();
    }

    public Pet updatePet(Pet request, int statusCode) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(request)
                .when().log().all()
                .put()
                .then().log().all()
                .contentType(ContentType.JSON)
                .statusCode(statusCode)
                .extract().as(Pet.class);
    }

    public Response deletePet(Object id, int statusCode) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .pathParam("id", id)
                .when().log().all()
                .delete(PET_BY_ID)
                .then().log().all()
                .statusCode(statusCode)
                .extract().response();
    }
}