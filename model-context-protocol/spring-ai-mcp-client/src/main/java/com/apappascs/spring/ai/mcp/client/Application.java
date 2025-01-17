package com.apappascs.spring.ai.mcp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.client.McpSyncClient;
import org.springframework.ai.mcp.client.transport.ServerParameters;
import org.springframework.ai.mcp.client.transport.StdioClientTransport;
import org.springframework.ai.mcp.spring.McpFunctionCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Value("${brave.api-key}")
	private String braveApiKey;

	@Bean
	public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder,
			McpSyncClient mcpClient, ConfigurableApplicationContext context) {

		return args -> {

			var chatClient = chatClientBuilder
					.defaultFunctions(mcpClient.listTools(null)
							.tools()
							.stream()
							.map(tool -> new McpFunctionCallback(mcpClient, tool))
							.toArray(McpFunctionCallback[]::new))
					.build();

			String question = "Does Spring AI supports the Model Context Protocol? Please provide some references.";
			log.info("QUESTION: {}", question);
			log.info("ASSISTANT: {}", chatClient.prompt(question).call().content());

			context.close();
		};
	}

	@Bean(destroyMethod = "close")
	public McpSyncClient dockerClient() {
		var stdioParams = ServerParameters.builder("docker")
				.args("run", "-i", "--rm", "-e", "BRAVE_API_KEY", "mcp/brave-search")
				.addEnvVar("BRAVE_API_KEY", braveApiKey)
				.build();

		var mcpClient = McpClient.using(new StdioClientTransport(stdioParams)).sync();
		var init = mcpClient.initialize();
		System.out.println("MCP Initialized (Docker): " + init);
		return mcpClient;
	}

}