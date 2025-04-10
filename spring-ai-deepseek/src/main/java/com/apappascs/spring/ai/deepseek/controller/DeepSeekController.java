package com.apappascs.spring.ai.deepseek.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.apappascs.spring.ai.deepseek.tools.DateTimeTools;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/deepseek")
public class DeepSeekController {

    private final ChatClient.Builder chatClientBuilder;
    private final DateTimeTools dateTimeTools;

    @Autowired
    public DeepSeekController(ChatClient.Builder chatClientBuilder, DateTimeTools dateTimeTools) {
        this.chatClientBuilder = chatClientBuilder;
        this.dateTimeTools = dateTimeTools;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String jsonPrompt) {
        return chatClientBuilder.build()
                .prompt(jsonPrompt)
                .tools(dateTimeTools)
                .call().content();
    }

    @PostMapping("/chat/stream")
    public Flux<String> chatStream(@RequestBody String jsonPrompt) {
        return chatClientBuilder.build()
                .prompt(jsonPrompt)
                .tools(dateTimeTools)
                .stream()
                .content();
    }
}