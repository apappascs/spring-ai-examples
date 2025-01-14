package com.apappascs.spring.ai.tavily.agent;

import com.apappascs.spring.ai.tavily.service.TavilyApiClient;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QueryFormulationAgent {

	private final ChatClient chatClient;

	public QueryFormulationAgent(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder
				.defaultSystem("""
                        You are an AI assistant specializing in generating structured search queries for the Tavily API.
                        Your role is to analyze the user input and construct an effective query object.
                        Please keep the Default values for each TavilyRequest field in mind when formulating the query.
                        
                        Here are the key fields you should fill in:
                        - **query**: Extract the main search intent or keywords from the user's input.
                        - **topic**: If the user's input hints at a specific domain (e.g., technology, healthcare, etc.), include it.
                        - **searchDepth**: Determine whether the search should be "basic" or "detailed" based on user needs.
                        - **days**: Default to 300 days unless the user specifies a shorter or longer timeframe.
                        - **maxResults**: If unspecified, default to 10 results.
                        - **includeImages** and **includeImageDescriptions**: Set to true if the user asks for visual content.
                        - **includeAnswer** and **includeRawContent**: Enable these fields to improve search comprehensiveness.
                        - **includeDomains** and **excludeDomains**: If the user specifies domains to focus on or avoid, capture them.
                        
                        Your response should always generate a valid JSON representation of the TavilyRequest object.
                        """)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.build();
	}

	public TavilyApiClient.TavilyRequest formulateQuery(String userInput) {
		log.info("Formulating query for user input: {}", userInput);

		return chatClient.prompt()
				.user(userInput)
				.call()
				.entity(TavilyApiClient.TavilyRequest.class);
	}
}