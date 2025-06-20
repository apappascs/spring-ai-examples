package com.apappascs.spring.ai.elevenlabs.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
@AutoConfigureWebTestClient
class SpeechControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void speakEndpoint_shouldReturnAudio() {
        webTestClient.post()
                .uri("/tts/elevenlabs")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Hello, this is a test of the ElevenLabs text to speech API.")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("audio/mpeg")
                .expectBody(byte[].class)
                .value(audioData -> assertThat(audioData.length, greaterThan(0)));
    }

    @Test
    void streamEndpoint_shouldReturnStreamingAudio() {
        webTestClient.post()
                .uri("/tts/elevenlabs/stream")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("This is a streaming audio test. The audio should be delivered in chunks.")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("audio/mpeg")
                .expectBody(byte[].class)
                .value(responseBody -> assertThat(responseBody.length, greaterThan(0)));
    }
}