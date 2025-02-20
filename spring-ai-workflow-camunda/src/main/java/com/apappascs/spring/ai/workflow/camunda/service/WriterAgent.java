package com.apappascs.spring.ai.workflow.camunda.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("writerAgent")
public class WriterAgent implements JavaDelegate {

	private final ChatClient chatClient;

	public WriterAgent(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder
				.defaultSystem("""
                        You are an AI assistant tasked with writing concise and engaging blog posts.
                        Given a user topic and search results, your goal is to craft a short blog post that explains the topic.
                        The blog post should be easy to understand, well-organized, and should be no more than 250 words.

                        Consider this structure:

                        1.  **Introduction:** Briefly introduce the topic.
                        2.  **Key Points:** Highlight the most important aspects, benefits, or findings from the search results.
                        3.  **Conclusion:** Briefly summarize the key takeaways.

                        **Input:**
                        -   `userTopic`: The topic of the blog post.
                        -   `searchResults`: A JSON object containing search results.

                        **Output:**
                        -   A concise and well-written blog post. Do NOT use Markdown formatting.
                        """)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.build();
	}

	@Override
	public void execute(DelegateExecution execution) {
		log.info("WriterAgent: Starting to write blog post...");

		String userTopic = (String) execution.getVariable("userTopic");
		if (userTopic == null || userTopic.isEmpty()) {
			userTopic = "General Topic";
		}

		Object searchResults = execution.getVariable("searchResults");
		if (searchResults == null) {
			searchResults = "No search results available.";
		}

		String prompt = String.format("""
                User Topic: %s

                Search Results: %s
                """, userTopic, searchResults);

		String blogPostContent = chatClient.prompt()
				.user(prompt)
				.call()
				.content();

		execution.setVariable("blogPostContent", blogPostContent);
		log.info("WriterAgent: Blog post written.");
	}
}
