import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class MedicalProfileIntegrationTest {
    @BeforeAll
    static void setup() {
        // Set the base URI for the API gateway
        RestAssured.baseURI = "http://localhost:8084";
    }

    @Test
    public void shouldReturnMedicalProfileWithValidToken () {
        String loginPayload = """
                    {
                        "email": "testpriti@test.com",
                        "password": "password123"
                    }
                """;

        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");

        given()
                .header("Authorization", "Bearer " + token) // Set the Authorization header with the Bearer token
                .when()
                .get("/api/medical-profiles") // Call the endpoint
                .then()
                .statusCode(200) // Assert that the response status code is 200 OK
                .body("medicalProfiles", notNullValue()); // Assert that the response body contains a medicalProfile field that is not null
    }
}
