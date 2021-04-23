package org.redhat;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import java.util.Random;
import java.util.UUID;

@QuarkusTest
public class NotesResourceTest {

    @Test
    public void testPrometheusMetrics() {
        // test whether the metrics endpoint works
        given().when().get("/q/metrics").then().statusCode(200);
    }

    @Test
    public void testGetAllEndpoint() {
        // list all, it should contain every row loaded with import.sql
        given().when().get("/notes/all").then().statusCode(200).body(containsString("test note 1"),
                containsString("test note 2"), containsString("test note 3"), containsString("test note 4"));
    }

    @Test
    public void testGetByIdEndpoint() {
        // search for a specific ID
        Long noteId = new Long(1002);
        given().pathParam("itemId", noteId).when().get("/notes/{itemId}").then().statusCode(200)
                .body(containsString("buy some books plz"));
    }

    @Test
    public void testGetANonExistingNote() {
        Random rng = new Random();
        Long noteId = rng.nextLong();

        // search for a non-existent note id
        given().pathParam("itemId", noteId).when().get("/notes/{itemId}").then().statusCode(404);
    }

    @Test
    public void testPublishEndpoint() {
        // publish a new note
        String newName = UUID.randomUUID().toString();
        String newContents = UUID.randomUUID().toString();

        String requestBody = "{\"name\": \"NAME_HOLDER\", \"contents\": \"CONTENT_HOLDER\"}";
        Response result = given()
                            .when()
                            .body(requestBody
                                .replace("NAME_HOLDER", newName)
                                .replace("CONTENT_HOLDER", newContents)
                            ).contentType("application/json")
                            .post("/notes/publish")
                            .then()
                                .statusCode(200).extract().response();

        // ensure the note is there
        given()
            .when()
                .pathParam("itemId", result.getBody().asString())
                .get("/notes/{itemId}")
            .then().statusCode(200)
                .body(
                    containsString(newContents),
                    containsString(newName)
                    );

        // remove the note
        given()
            .pathParam("itemId", result.getBody().asString())
            .when().delete("/notes/{itemId}")
            .then()
                .statusCode(200);

    }

    @Test
    public void testUpdateEndpoint() {
        // publish a new note
        String newName = UUID.randomUUID().toString();
        String newContents = UUID.randomUUID().toString();
        String requestBody = "{\"name\": \"NAME_HOLDER\", \"contents\": \"CONTENT_HOLDER\"}";

        // first, publish a note
        Response result = given()
                            .when()
                            .body(requestBody
                                .replace("NAME_HOLDER", newName)
                                .replace("CONTENT_HOLDER", newContents)
                            ).contentType("application/json")
                            .post("/notes/publish")
                            .then()
                                .statusCode(200).extract().response();
        
        // then update the note
        // update an existing note
        String updatedContent = UUID.randomUUID().toString();
        String updatedName = UUID.randomUUID().toString();
        given()
            .pathParam("itemId", result.getBody().asString())
            .when()
            .body(requestBody
                    .replace("NAME_HOLDER", updatedName)
                    .replace("CONTENT_HOLDER", updatedContent)
                ).contentType("application/json")
            .put("/notes/{itemId}")
            .then()
                .statusCode(200);
        
        // make sure the updated values are there
        given()
            .pathParam("itemId", result.getBody().asString())
            .when().get("/notes/{itemId}")
            .then()
                .statusCode(200)
                .body(
                    containsString(updatedContent),
                    containsString(updatedName)
                );

        // check by getting all notes
        given()
            .when().get("/notes/all")
            .then().statusCode(200)
            .body(
                not(containsString(newName)),
                not(containsString(newContents))
            );
        
        // finally remove the note
        given()
            .pathParam("itemId", result.getBody().asString())
            .when().delete("/notes/{itemId}")
            .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteANonExistingNote() {
        Random rng = new Random();
        Long noteId = rng.nextLong();

        // try to delete a non-existent note id
        given()
            .pathParam("itemId", noteId)
            .when().delete("/notes/{itemId}")
            .then()
                .statusCode(404);
    }
}