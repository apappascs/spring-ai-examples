package com.apappascs.spring.ai.workflow.camunda.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

@Slf4j
@Service("searchAgent")
public class SearchAgent implements JavaDelegate {

	private final ChatClient chatClient;
	private final TavilyApiClient tavilyApiClient;

	public SearchAgent(ChatClient.Builder chatClientBuilder, TavilyApiClient tavilyApiClient) {
		this.chatClient = chatClientBuilder
				.defaultSystem("""
                        You are an AI assistant specializing in generating structured search queries for the Tavily API.
                        Your role is to analyze the user input and construct an effective query object.
                        Please keep the Default values for each TavilyRequest field in mind when formulating the query.
                        
                        Here are the key fields you should fill in:
                        - **query**: Extract the main search intent or keywords from the user's input.
                        - **topic**: Accepted values: 'general', 'news'
                        - **searchDepth**: Determine whether the search should be "basic" or "detailed" based on user needs. Set to basic unless specified otherwise.
                        - **days**: Default to 300 days unless the user specifies a shorter or longer timeframe.
                        - **maxResults**: If unspecified, default to 10 results.
                        - **includeImages** and **includeImageDescriptions**: Set to true if the user asks for visual content.
                        - **includeAnswer** and **includeRawContent**: Enable these fields to improve search comprehensiveness.
                        - **includeDomains** and **excludeDomains**: If the user specifies domains to focus on or avoid, capture them.
                        
                        Your response should always generate a valid JSON representation of the TavilyRequest object.
                        """)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.build();
		this.tavilyApiClient = tavilyApiClient;
	}

	public TavilyApiClient.TavilyRequest formulateQuery(String userInput) {
		log.info("Formulating query for user input: {}", userInput);

		return chatClient.prompt()
				.user(userInput)
				.call()
				.entity(TavilyApiClient.TavilyRequest.class);
	}

	@Override
	public void execute(DelegateExecution execution) {
		log.info("SearchAgent: Starting search...");
		String userTopic = "Spring AI and Camunda";
		execution.setVariable("userTopic", userTopic);
		TavilyApiClient.TavilyRequest springAiAndCamunda = formulateQuery(userTopic);
		TavilyApiClient.TavilyResponse searchResults = tavilyApiClient.search(springAiAndCamunda);
		execution.setVariable("searchResults", searchResults);
		log.info("SearchAgent: Search complete. Results stored.");
	}
}
