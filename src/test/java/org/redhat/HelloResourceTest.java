package org.redhat;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HelloResourceTest {

    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

    @Test
    void testHelloNameEndpoint() {
        String randomName = UUID.randomUUID().toString();
        given()
            .pathParam("name", randomName)
            .when().get("/hello/{name}")
            .then()
                .statusCode(200)
                .body(is("Hello " + randomName + "!"));
    }

}