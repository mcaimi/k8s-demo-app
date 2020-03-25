package org.redhat;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import java.util.UUID;

@QuarkusTest
public class NotesResourceTest {

    @Test
    public void testGetAllEndpoint() {
        // list all, it should contain every row loaded with import.sql
        given()
          .when().get("/notes/all")
          .then()
             .statusCode(200)
             .body(
                containsString("test note 1"),
                containsString("test note 2"),
                containsString("test note 3"),
                containsString("test note 4")
                );

        // delete one item
        given()
            .pathParam("item_id", new Long(1001))
            .when().delete("/notes/{item_id}")
            .then().statusCode(200);
        
        // relist all, the third item should be gone
        given()
          .when().get("/notes/all")
          .then()
             .statusCode(200)
             .body(
                containsString("test note 1"),
                containsString("test note 2"),
                not(containsString("test note 3")),
                containsString("test note 4")
                );
    }

    @Test
    public void testGetByIdEndpoint() {
        // search for a specific ID
        Long noteId = new Long(1002);
        given()
            .pathParam("item_id", noteId)
            .when().get("/notes/{item_id}")
            .then()
                .statusCode(200)
                .body(
                    containsString("buy some books plz")
                );
    }

    @Test
    public void testPublishEndpoint() {
        // publish a new note
        String newName = UUID.randomUUID().toString();
        String newContents = UUID.randomUUID().toString();
    
        String requestBody = "{\"name\": \"NAME_HOLDER\", \"contents\": \"CONTENT_HOLDER\"}";
        given()
            .when()
            .body(requestBody
                    .replace("NAME_HOLDER", newName)
                    .replace("CONTENT_HOLDER", newContents)
                ).contentType("application/json")
            .post("/notes/publish")
            .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateEndpoint() {
        // update an existing note
        String updatedContent = UUID.randomUUID().toString();
        String updatedName = UUID.randomUUID().toString();
        String requestBody = "{\"name\": \"NAME_HOLDER\", \"contents\": \"CONTENT_HOLDER\"}";
        given()
            .pathParam("item_id", new Long(1002))
            .when()
            .body(requestBody
                    .replace("NAME_HOLDER", updatedName)
                    .replace("CONTENT_HOLDER", updatedContent)
                ).contentType("application/json")
            .put("/notes/{item_id}")
            .then()
                .statusCode(200);
        
        // make sure the updated values are there
        given()
            .pathParam("item_id", new Long(1002))
            .when().get("/notes/{item_id}")
            .then()
                .statusCode(200)
                .body(
                    containsString(updatedContent),
                    containsString(updatedName)
                );
    }
}