package com.apappascs.spring.ai.tts.controller;

import com.apappascs.spring.ai.tts.service.InteractiveAssistantService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests for TtsUiController.
 * Uses mocked Spring AI services to avoid requiring real API keys.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("openai")
@TestPropertySource(properties = {
        "spring.ai.model.audio.speech=openai",
        "spring.ai.openai.api-key=test-key"
})
@AutoConfigureWebTestClient
class TtsUiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InteractiveAssistantService assistantService;

    @MockBean
    private TextToSpeechModel textToSpeechModel;

    @Test
    void indexEndpoint_shouldReturnIndexView() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("Spring AI Assistant"));
                    assertThat(body, containsString("ai-orb"));
                    assertThat(body, containsString("callButton"));
                });
    }

    @Test
    void streamGreeting_shouldReturnAudioStream() {
        // Mock the service to return audio bytes
        byte[] mockAudioData = new byte[]{0x01, 0x02, 0x03, 0x04};
        when(assistantService.streamTextToSpeech(anyString()))
                .thenReturn(Flux.just(mockAudioData));

        webTestClient.get()
                .uri("/voice/greeting/stream")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class)
                .value(audioData -> {
                    assertThat(audioData, notNullValue());
                    assertThat(audioData.length, greaterThan(0));
                });
    }

    @Test
    void streamGreeting_shouldStreamExpectedGreeting() {
        // Verify it calls the service with the correct greeting text
        byte[] mockAudioData = new byte[]{0x01, 0x02, 0x03, 0x04};
        when(assistantService.streamTextToSpeech("Hello! How can I help you today?"))
                .thenReturn(Flux.just(mockAudioData));

        webTestClient.get()
                .uri("/voice/greeting/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(audioData -> assertThat(audioData, notNullValue()));
    }

    @Test
    void streamVoiceChat_shouldAcceptAudioAndReturnResponse() {
        // Mock the voice response stream
        byte[] mockAudioResponse = new byte[]{0x10, 0x20, 0x30, 0x40};
        when(assistantService.streamVoiceResponse(any()))
                .thenReturn(Flux.just(mockAudioResponse));

        // Create mock audio file
        byte[] mockAudioInput = "mock audio data".getBytes();
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("audio", new ByteArrayResource(mockAudioInput) {
            @Override
            public String getFilename() {
                return "test-audio.webm";
            }
        }).contentType(MediaType.parseMediaType("audio/webm"));

        webTestClient.post()
                .uri("/voice/chat/stream")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class)
                .value(audioData -> {
                    assertThat(audioData, notNullValue());
                    assertThat(audioData.length, greaterThan(0));
                });
    }

    @Test
    void streamVoiceChat_shouldHandleEmptyAudioGracefully() {
        // When audio part is missing, expect error (could be 4xx or 5xx depending on framework)
        webTestClient.post()
                .uri("/voice/chat/stream")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .exchange()
                .expectStatus().isEqualTo(500); // WebFlux returns 500 for missing required @RequestPart
    }

    @Test
    void getProvider_shouldReturnProviderName() {
        // The mocked TextToSpeechModel class name will be returned
        webTestClient.get()
                .uri("/tts/provider")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(providerName -> {
                    assertThat(providerName, notNullValue());
                    // Will contain "Mock" since we're using @MockBean
                    assertThat(providerName, containsString("Mock"));
                });
    }

    @Test
    void getProvider_shouldReturnTextContentType() {
        webTestClient.get()
                .uri("/tts/provider")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN);
    }
}