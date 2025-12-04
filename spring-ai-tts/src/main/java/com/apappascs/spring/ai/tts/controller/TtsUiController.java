package com.apappascs.spring.ai.tts.controller;

import com.apappascs.spring.ai.tts.service.InteractiveAssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive Voice Assistant Controller using WebFlux.
 * All endpoints stream audio data for better performance.
 */
@Controller
public class TtsUiController {

    private static final Logger logger = LoggerFactory.getLogger(TtsUiController.class);
    private final InteractiveAssistantService assistantService;
    private final TextToSpeechModel textToSpeechModel;
    private static final String GREETING = "Hello! How can I help you today?";

    public TtsUiController(InteractiveAssistantService assistantService, TextToSpeechModel textToSpeechModel) {
        this.assistantService = assistantService;
        this.textToSpeechModel = textToSpeechModel;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    /**
     * Stream greeting as audio chunks.
     * GET /voice/greeting/stream
     */
    @GetMapping(value = "/voice/greeting/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public Flux<byte[]> streamGreeting() {
        logger.info("Streaming greeting");
        return assistantService.streamTextToSpeech(GREETING);
    }

    /**
     * Stream voice response: Transcribe → Streaming Chat → Streaming TTS.
     * POST /voice/chat/stream with multipart file
     */
    @PostMapping(value = "/voice/chat/stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public Flux<byte[]> streamVoiceChat(@RequestPart("audio") Mono<FilePart> filePart) {
        logger.info("Received streaming voice chat request");

        return filePart
                .flatMapMany(part -> {
                    // Convert FilePart to byte array Resource
                    return DataBufferUtils.join(part.content())
                            .map(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                return new org.springframework.core.io.ByteArrayResource(bytes) {
                                    @Override
                                    public String getFilename() {
                                        return part.filename();
                                    }
                                };
                            })
                            .flatMapMany(assistantService::streamVoiceResponse);
                });
    }

    /**
     * Get the current TTS provider name.
     * GET /tts/provider
     */
    @GetMapping("/tts/provider")
    @ResponseBody
    public Mono<String> getProvider() {
        String providerName = textToSpeechModel.getClass().getSimpleName();
        logger.info("Current TTS provider: {}", providerName);
        return Mono.just(providerName);
    }
}
