package com.apappascs.spring.ai.tavily.agent;

import com.apappascs.spring.ai.tavily.service.TavilyApiClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ResponseCurationAgentTests {

	@Autowired
	private ResponseCurationAgent responseCurationAgent;

	@Test
	void curateResponse_shouldGenerateDetailedSummary() {
		String userMessage = "Tell me about the latest advancements in AI.";

		// Mock TavilyResponse
		TavilyApiClient.TavilyResponse response = new TavilyApiClient.TavilyResponse();
		response.setQuery("latest advancements in AI");
		response.setAnswer("Artificial Intelligence (AI) has seen major advancements recently...");
		response.setResults(List.of(
				new TavilyApiClient.TavilyResponse.Result(
						"AI Breakthroughs",
						"https://example.com/ai-breakthroughs",
						"AI is evolving...",
						null,
						0.9f,
						"2025-01-01"
				),
				new TavilyApiClient.TavilyResponse.Result(
						"AI in Healthcare",
						"https://example.com/ai-healthcare",
						"AI is transforming healthcare...",
						null,
						0.8f,
						"2025-01-10"
				)
		));

		// Invoke the response curation method
		Flux<String> curatedResponseFlux = responseCurationAgent.curateResponse(userMessage, response);

		// Collect the entire response into a single string for easier assertions
		String fullCuratedResponse = curatedResponseFlux.collectList()
				.map(list -> String.join("", list)) // Join the list of tokens into a single string
				.block(); // Block until the full response is available

		log.info("Full Curated Response: {}", fullCuratedResponse);

		// Assertions on the complete response
		assertNotNull(fullCuratedResponse, "The curated response should not be null.");
		assertTrue(fullCuratedResponse.contains("[AI Breakthroughs](https://example.com/ai-breakthroughs)"),
				"The response should include the 'AI Breakthroughs' source link.");
		assertTrue(fullCuratedResponse.contains("[AI in Healthcare](https://example.com/ai-healthcare)"),
				"The response should include the 'AI in Healthcare' source link.");
	}
}