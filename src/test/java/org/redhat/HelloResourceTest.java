package org.redhat;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class HelloResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

    @Test
    public void testHelloNameEndpoint() {
        String randomName = UUID.randomUUID().toString();
        given()
            .when().get("/hello/" + randomName)
            .then()
                .statusCode(200)
                .body(is("Hello " + randomName + "!"));
    }

}