package com.apappascs.spring.ai.tts.service;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Reactive service for interactive assistant supporting streaming voice operations.
 * All operations use reactive streaming for better performance and responsiveness.
 */
@Service
public class InteractiveAssistantService {

    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;
    private final TranscriptionModel transcriptionModel;

    public InteractiveAssistantService(
            ChatClient.Builder chatClientBuilder,
            TextToSpeechModel textToSpeechModel,
            TranscriptionModel transcriptionModel) {

        this.chatClient = chatClientBuilder
                .defaultSystem("You are a helpful and friendly assistant. Keep responses concise and natural.")
                .build();
        this.textToSpeechModel = textToSpeechModel;
        this.transcriptionModel = transcriptionModel;
    }

    /**
     * Stream text to speech - returns audio chunks as they're generated.
     * Enables client to start playback before complete generation.
     */
    public Flux<byte[]> streamTextToSpeech(String text) {
        return textToSpeechModel.stream(text);
    }

    /**
     * Stream voice response: Transcribe → Chat → Streaming TTS.
     * Collects complete chat response first, then streams the audio.
     */
    public Flux<byte[]> streamVoiceResponse(Resource audioFile) {
        // 1. Transcribe audio to text (blocking operation wrapped in Mono)
        return Mono.fromCallable(() -> transcribeAudio(audioFile))
                .subscribeOn(Schedulers.boundedElastic()) // Execute blocking call on separate thread pool
                // 2. Get streaming chat response and collect all text chunks
                .flatMapMany(transcribedText -> chatClient.prompt()
                        .user(transcribedText)
                        .stream()
                        .content()
                        .reduce("", (acc, chunk) -> acc + chunk)) // Collect complete response
                // 3. Convert complete text to speech and stream audio chunks
                .flatMap(textToSpeechModel::stream);
    }

    /**
     * Transcribe audio file to text (blocking operation).
     */
    private String transcribeAudio(Resource audioFile) {
        AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(audioFile);
        return transcriptionModel.call(transcriptionPrompt).getResult().getOutput();
    }
}