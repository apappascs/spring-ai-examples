package com.apappascs.spring.ai.tts.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * Reactive streaming TTS controller using WebFlux.
 */
@RestController
@RequestMapping("/tts")
public class StreamingTtsController {

    private static final Logger logger = LoggerFactory.getLogger(StreamingTtsController.class);
    private final TextToSpeechModel textToSpeechModel;

    public StreamingTtsController(TextToSpeechModel textToSpeechModel) {
        this.textToSpeechModel = textToSpeechModel;
    }

    /**
     * Stream text-to-speech audio chunks as they're generated.
     */
    @PostMapping(value = "/stream", produces = "audio/mpeg")
    public Flux<byte[]> stream(@RequestBody String text) {
        logger.info("Streaming TTS for text: '{}'", text);
        return textToSpeechModel.stream(text)
                .doOnComplete(() -> logger.info("Streaming completed successfully"))
                .doOnError(error -> logger.error("Error during streaming", error));
    }
}
