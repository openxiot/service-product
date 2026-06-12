package cc.openxiot.product.api.namespace;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class NamespaceResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/v1/namespace")
          .then()
             .statusCode(200)
             .body(is("Hello from NamespaceResource"));
    }

}