package com.apappascs.spring.ai.deepseek.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
public class DeepSeekControllerTests {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void chatEndpoint_shouldReturnContent() {
        given()
                .contentType("application/json")
                .body("{\"prompt\":\"What time is it?\"}")
                .when()
                .post("/api/deepseek/chat")
                .then()
                .statusCode(200)
                .body(not(emptyString()));
    }

    @Test
    void chatStreamEndpoint_shouldReturnFluxContent() {
        Response response = given()
                .contentType("application/json")
                .body("{\"prompt\":\"Stream this\"}")
                .when()
                .post("/api/deepseek/chat/stream");

        // Validate status
        assertThat(response.statusCode(), equalTo(200));

        // Validate stream-like content (basic check)
        String body = response.getBody().asString();
        assertThat(body, not(emptyString()));
        System.out.println("Streamed response:\n" + body);
    }
}