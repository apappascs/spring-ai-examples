package com.apappascs.spring.ai.tts.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Integration tests for StreamingTtsController.
 * Note: These tests require valid API keys to actually call the TTS services.
 * Set OPENAI_API_KEY or ELEVENLABS_API_KEY environment variable.
 */
//@Disabled("Requires valid API key - enable manually for integration testing")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("openai")
@TestPropertySource(properties = {
        "spring.ai.model.audio.speech=openai",
        "spring.ai.openai.api-key=test-key"
})
@AutoConfigureWebTestClient
class StreamingTtsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void streamEndpoint_shouldReturnStreamingAudio() {
        webTestClient.post()
                .uri("/tts/stream")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("This is a streaming audio test. Audio should be delivered in chunks.")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("audio/mpeg")
                .expectBody(byte[].class)
                .value(audioData -> {
                    assertThat(audioData, notNullValue());
                    assertThat(audioData.length, greaterThan(0));
                });
    }
}
