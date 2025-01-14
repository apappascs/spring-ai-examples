package com.apappascs.spring.ai.tavily.agent;

import com.apappascs.spring.ai.tavily.service.TavilyApiClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class QueryFormulationAgentTests {

	@Autowired
	private QueryFormulationAgent queryFormulationAgent;

	@Test
	void formulateQuery_shouldGenerateValidRequest() {
		String userInput = "Find the latest articles on Spring AI in the last 30 days.";

		// Invoke the query formulation method
		TavilyApiClient.TavilyRequest request = queryFormulationAgent.formulateQuery(userInput);

		// Validate the generated request
		assertNotNull(request, "The generated TavilyRequest should not be null.");
		assertNotNull(request.getQuery(), "The query field in TavilyRequest should not be null.");
		assertTrue(request.getQuery().contains("Spring AI"), "The query should contain the term 'Spring AI'.");
		assertTrue(request.getDays() <= 30, "The days field should not exceed 30.");

		log.info("Generated TavilyRequest: {}", request);
	}
}
