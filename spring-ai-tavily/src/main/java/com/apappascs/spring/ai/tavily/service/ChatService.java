package com.apappascs.spring.ai.tavily.service;

import com.apappascs.spring.ai.tavily.agent.QueryFormulationAgent;
import com.apappascs.spring.ai.tavily.agent.ResponseCurationAgent;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final QueryFormulationAgent queryFormulationAgent;
	private final ResponseCurationAgent responseCurationAgent;
	private final TavilyApiClient tavilyApiClient;

	/**
	 * Handles the flow of query formulation, Tavily search, and response curation to generate a curated response.
	 *
	 */
	public Flux<String> generateCuratedResponse(String userInput) {
		// Formulate the query
		TavilyApiClient.TavilyRequest tavilyRequest = queryFormulationAgent.formulateQuery(userInput);
		// Perform the Tavily search
		TavilyApiClient.TavilyResponse tavilyResponse = tavilyApiClient.search(tavilyRequest);
		// Curate the response
		return responseCurationAgent.curateResponse(userInput, tavilyResponse);
	}
}
