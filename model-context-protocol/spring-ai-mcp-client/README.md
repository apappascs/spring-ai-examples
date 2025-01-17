# Spring AI MCP Client with Brave Search Example

This project demonstrates a Spring AI client that uses the [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) to interact with an **MCP Server**. Specifically, it shows how to integrate with the [Brave Search](https://search.brave.com/) MCP server, one of the servers available in the official [MCP servers repository](https://github.com/modelcontextprotocol/servers).

The application uses a `ChatClient` configured with `McpFunctionCallback` to interact with the Brave Search MCP server via standard input and output (stdio). The client can then ask questions, and the Brave Search server will respond through the MCP protocol, providing search results as if it were a tool.

## Prerequisites

*   Java 17 or higher
*   [Docker](https://www.docker.com/products/docker-desktop)
*   OpenAI API Key - set as environment variable `OPENAI_API_KEY`
*   Brave API Key - set as environment variable `BRAVE_API_KEY`

## Getting Started

1. **Clone the repository:**

    ```bash
    git clone https://github.com/apappascs/spring-ai-examples.git
    cd spring-ai-examples/model-context-protocol/spring-ai-mcp-client
    ```

2. **Set environment variables:**

    ```bash
    export OPENAI_API_KEY=<your-openai-api-key>
    export BRAVE_API_KEY=<your-brave-api-key>
    ```

3. **Build and run the application (using Gradle):**

    ```bash
    ./gradlew bootRun
    ```

   This will start the Spring Boot application. It will run a predefined question that is logged in the console, demonstrating the interaction with the Brave Search MCP server via MCP.

## How it Works

*   The application initializes an `McpSyncClient` configured to use `StdioClientTransport`. This client communicates with a Docker container running the Brave Search MCP server via standard input/output.
*   The `dockerClient` bean is responsible for starting and managing the Docker container that runs the Brave Search MCP server. The server exposes its capabilities (in this case, search functionality) through the MCP protocol.
*   The `predefinedQuestions` bean is a `CommandLineRunner` that sends a predefined question to the `ChatClient`.
*   The `ChatClient` is configured to support the Model Context Protocol by passing it `McpFunctionCallback`s via its builder. These callbacks allow the `ChatClient` to discover and use the tools exposed by the MCP server.
*   When the `ChatClient` determines that the Brave Search tool should be used, it invokes the corresponding `McpFunctionCallback`. This callback sends the tool execution request to the Brave Search MCP server via the `McpSyncClient`, and the server returns the search results.

## Technologies Used

*   [Spring AI](https://docs.spring.io/spring-ai/reference/): A framework for building AI-powered applications.
*   [Spring AI MCP](https://docs.spring.io/spring-ai-mcp/reference/overview.html): An experimental project that provides a Java and Spring Framework integration for the Model Context Protocol.
*   [Brave Search](https://search.brave.com/): A privacy-focused search engine with a developer API.
*   [Spring Boot](https://spring.io/projects/spring-boot): A framework for building stand-alone, production-grade Spring applications.
*   [Gradle](https://gradle.org/): A build automation tool.
*   [Docker](https://www.docker.com/): A platform for developing, shipping, and running applications in containers.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the project.