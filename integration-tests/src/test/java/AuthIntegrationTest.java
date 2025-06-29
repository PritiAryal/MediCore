import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8084";
        // Set the base URI and this will happen before all tests run.
        // It is address of our API gateway when all containers are running inside Docker.
    }

    @Test
    public void shouldReturnOKWithValidToken() { // Naming of test is imp. Different companies typically has a standard when it comes to test name so that its easy to see at a glance what this test is going to do.
        // the naming convension we are using here is: should _<expected outcome> _ with _ <condition i.e. this data>

        // 3 steps to create a good test.
        // 1. Arrange: Prepare the data and the environment for the test
        // 2. Act: Call the method or endpoint that you want to test
        // 3. Assert: Check the result of the method or endpoint against the expected outcome

        // This test checks that the login endpoint returns a 200 OK status code and a valid token when valid credentials are provided.
        String loginPayload = """
                    {
                        "email": "testpriti@test.com",
                        "password": "password123"
                    }
                """;
        // This is a JSON string that represents the login request payload.

        // write the code that will actually runs the test

        // Arranged using given keyword and Acted using when keyword and Asserted using then keyword.
        Response response = given()
                .contentType("application/json") // Set the content type to application/json
                .body(loginPayload) // Set the body of the request to the login payload
                .when()
                .post("/auth/login") // Call the /login endpoint
                .then()
                .statusCode(200) // Assert that the response status code is 200 OK
                .body("token", notNullValue()) // Assert that the response body contains a token field that is not null
                .extract()
                .response(); // Extract the response object for further use.

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
        // This will print the generated token to the console.
    }


    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin() {
        // This test checks that the login endpoint returns a 401 Unauthorized status code when invalid credentials are provided.
        String loginPayload = """
                    {
                        "email": "invalid_user@test.com",
                        "password": "wrongpassword"
                    }
                """;

        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401) ;

    }
}
