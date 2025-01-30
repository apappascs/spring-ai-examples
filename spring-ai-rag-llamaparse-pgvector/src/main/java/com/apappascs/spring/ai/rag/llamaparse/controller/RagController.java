package com.apappascs.spring.ai.rag.llamaparse.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class RagController {

	private final ChatClient chatClient;
	private final QuestionAnswerAdvisor questionAnswerAdvisor;

	RagController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
		this.chatClient = chatClientBuilder.build();
		this.questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
				.searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build()).build();
	}

	@PostMapping("/rag/chat")
	String chatWithDocument(@RequestBody String question) {
		return chatClient.prompt()
				.advisors(questionAnswerAdvisor)
				.user(question)
				.call()
				.content();
	}

}
