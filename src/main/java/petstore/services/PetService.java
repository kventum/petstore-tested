package petstore.services;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import petstore.models.pets.Pet;
import petstore.util.FileProvider;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static petstore.constants.Endpoints.*;

public class PetService extends BaseService {

    @Override
    protected String getBasePath() {
        return PET;
    }

    public Pet createPet(Pet request, int statusCode) {
        return post(EMPTY, request)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().as(Pet.class);
    }

    public Response getPets(int statusCode) {
        return get(FIND_BY_STATUS_EP)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public List<Pet> getPets(String status, int statusCode) {
        return get(FIND_BY_STATUS_EP, Map.of("status", status))
                .then()
                .spec(responseSpec(statusCode))
                .extract().jsonPath().getList(".", Pet.class);
    }

    public ExtractableResponse<Response> getPetById(Object id, int statusCode) {
        return get(PET_BY_ID, "id", id)
                .then()
                .spec(responseSpec(statusCode))
                .extract();
    }

    public Pet updatePet(Pet request, int statusCode) {
        return put(EMPTY, request)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().as(Pet.class);
    }

    public Response deletePet(Object id, int statusCode) {
        return delete(PET_BY_ID, "id", id)
                .then()
                .spec(responseSpec(statusCode))
                .extract().response();
    }

    public Response uploadImage(Long id, String metadata, String fileName, int statusCode) {
        return given()
                .spec(REQUEST_SPECIFICATION.contentType(ContentType.MULTIPART))
                .pathParam("id", id)
                .multiPart("additionalMetadata", metadata)
                .multiPart("file", FileProvider.getFile(fileName))
                .when().log().all()
                .post(UPLOAD_PET_IMAGE)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response uploadImage(Long id, String fileName, int statusCode) {
        return given()
                .spec(REQUEST_SPECIFICATION.contentType(ContentType.MULTIPART))
                .pathParam("id", id)
                .multiPart("file", FileProvider.getFile(fileName))
                .when().log().all()
                .post(UPLOAD_PET_IMAGE)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }

    public Response uploadImage(Long id, int statusCode) {
        return given()
                .spec(REQUEST_SPECIFICATION.contentType(ContentType.MULTIPART))
                .pathParam("id", id)
                .when().log().all()
                .post(UPLOAD_PET_IMAGE)
                .then()
                .spec(responseSpec(statusCode, ContentType.JSON))
                .extract().response();
    }
}
