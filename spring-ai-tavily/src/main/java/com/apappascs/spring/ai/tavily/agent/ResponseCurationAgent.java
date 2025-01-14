package com.apappascs.spring.ai.tavily.agent;

import java.net.ConnectException;

import com.apappascs.spring.ai.tavily.service.TavilyApiClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResponseCurationAgent {

	private final ChatClient chatClient;

	public ResponseCurationAgent(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder
				.defaultSystem("""
                        You are an AI assistant responsible for curating responses based on search results.
                        Your job is to take user queries and search results, and craft a well-structured,
                        conversational response that meets the following criteria:
                        
                        1. **Detailed Summary**:
                           Summarize the topic comprehensively using the information from the search results.
                           Highlight the most relevant insights in a user-friendly and professional tone.
                        
                        2. **Source References**:
                           Include a list of the most relevant sources in markdown format:
                           - [Source 1 Title](url1)
                           - [Source 2 Title](url2)
                           - [Source 3 Title](url3)
                           Provide up to 5 sources based on their relevance and credibility.

                        3. **Clarity & Accuracy**:
                           Ensure the response is concise, clear, and backed by the provided data.
                           Avoid speculation or making assumptions not supported by the search results.
                        
                        **Input**:
                        - `userMessage`: The original user query.
                        - `searchResults`: A JSON object containing the search results from the Tavily API.
                        
                        **Output**:
                        - A conversational response addressing the user query based on the search results.
                        """)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.build();
	}

	public Flux<String> curateResponse(String userMessage, TavilyApiClient.TavilyResponse response) {
		log.info("Curating response for userMessage: {} with search results", userMessage);

		String formattedInput = String.format("""
                User Message:
                %s

                Search Results:
                %s
                """, userMessage, response);

		return chatClient.prompt()
				.user(formattedInput)
				.stream()
				.content()
				.onErrorResume(throwable -> {
					if (throwable.getCause() instanceof ConnectException) {
						return Flux.just("Oops! It seems I’m having trouble reaching the server. Please give it a moment and try again soon.");
					} else {
						return Flux.just("Something went wrong on my end. I’ll look into it — feel free to try again shortly!");
					}
				});
	}
}
