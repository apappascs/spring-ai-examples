package com.apappascs.spring.ai.tavily.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class TavilyApiClientIntegrationTest {

	@Autowired
	private TavilyApiClient tavilyApiClient;

	@Test
	void testSearchSuccessWithRealApi() {

		TavilyApiClient.TavilyRequest request = TavilyApiClient.TavilyRequest.builder()
				.query("Artificial Intelligence")
				.searchDepth("basic")
				.topic("news")
				.days(30)
				.maxResults(5)
				.includeImages(false)
				.build();

		TavilyApiClient.TavilyResponse response = tavilyApiClient.search(request);

		assertNotNull(response);
		assertEquals("Artificial Intelligence", response.getQuery());
		assertNotNull(response.getResults());
		assertFalse(response.getResults().isEmpty(), "Results should not be empty");
		response.getResults().forEach(result -> {
			assertNotNull(result.getTitle(), "Title should not be null");
			assertNotNull(result.getUrl(), "URL should not be null");
		});
	}
}
