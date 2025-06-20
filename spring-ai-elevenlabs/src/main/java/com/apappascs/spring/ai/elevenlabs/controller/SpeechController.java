package com.apappascs.spring.ai.elevenlabs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechModel;
import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

@RestController
@RequestMapping("/tts/elevenlabs")
public class SpeechController {

    private static final Logger logger = LoggerFactory.getLogger(SpeechController.class);
    private final ElevenLabsTextToSpeechModel textToSpeechModel;

    @Autowired
    public SpeechController(ElevenLabsTextToSpeechModel textToSpeechModel) {
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping(produces = "audio/mpeg")
    public ResponseEntity<byte[]> speak(@RequestBody String text) {
        logger.info("Received blocking TTS request for: '{}'", text);

        ElevenLabsTextToSpeechOptions speechOptions = ElevenLabsTextToSpeechOptions.builder()
                .model("eleven_turbo_v2_5")
                .voiceId("9BWtsMINqrJLrRacOk9x")
                .outputFormat("mp3_44100_128")
                .build();

        TextToSpeechPrompt speechPrompt = new TextToSpeechPrompt(text, speechOptions);
        TextToSpeechResponse response = textToSpeechModel.call(speechPrompt);

        return ResponseEntity.ok(response.getResults().get(0).getOutput());
    }

    /**
     * This is the idiomatic way to stream binary data in Spring MVC.
     * The controller returns immediately, and a separate thread writes data
     * from the reactive Flux to the response OutputStream.
     */
    @PostMapping(value = "/stream", produces = "audio/mpeg")
    public ResponseEntity<StreamingResponseBody> stream(@RequestBody String text) {
        logger.info("Received streaming TTS request for: '{}'", text);

        ElevenLabsTextToSpeechOptions streamingOptions = ElevenLabsTextToSpeechOptions.builder()
                .model("eleven_turbo_v2_5")
                .voiceId("9BWtsMINqrJLrRacOk9x")
                .outputFormat("mp3_44100_128")
                .build();

        TextToSpeechPrompt speechPrompt = new TextToSpeechPrompt(text, streamingOptions);

        StreamingResponseBody responseBody = outputStream -> {
            // Subscribe to the Flux and write each chunk to the servlet's output stream
            textToSpeechModel.stream(speechPrompt)
                    .map(response -> response.getResults().get(0).getOutput())
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext(chunk -> {
                        try {
                            outputStream.write(chunk);
                            outputStream.flush();
                        } catch (IOException e) {
                            throw new RuntimeException("Error writing audio chunk to stream.", e);
                        }
                    })
                    // blockLast() is safe and necessary here to keep the connection open
                    // until the stream is complete.
                    .blockLast();
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(responseBody);
    }
}