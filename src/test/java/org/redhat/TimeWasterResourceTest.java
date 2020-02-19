package org.redhat;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class TimeWasterResourceTest {

    @Test
    public void testTimeWasterOKEndpoint() {
        given()
          .when().get("/services/delay/1000")
          .then()
             .statusCode(200)
             .body(is("Wasted a little time: 1000ms."));
    }

    @Test
    public void testTimeWasterOKLongWaitEndpoint() {
        given()
          .when().get("/services/delay/2500")
          .then()
             .statusCode(200)
             .body(is("Wasted a little time: 2500ms."));
    }

    @Test
    public void testTimeWasterWithCodeOKEndpoint() {
        given()
          .when().get("/services/delay/1000/200")
          .then()
             .statusCode(200)
             .body(is("Wasted a little time: 1000ms."));
    }

    @Test
    public void testTimeWasterWithCodeNotFoundEndpoint() {
        given()
          .when().get("/services/delay/500/404")
          .then()
             .statusCode(404)
             .body(is("Wasted a little time: 500ms."));
    }

    @Test
    public void testTimeWasterWithCodeServerErrorEndpoint() {
        given()
          .when().get("/services/delay/2000/500")
          .then()
             .statusCode(500)
             .body(is("Wasted a little time: 2000ms."));
    }

    @Test
    public void testTimeWasterWithCodeOKLongWaitEndpoint() {
        given()
          .when().get("/services/delay/3000/200")
          .then()
             .statusCode(200)
             .body(is("Wasted a little time: 3000ms."));
    }
}