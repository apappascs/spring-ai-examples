# Spring AI Tavily Example

This project demonstrates the integration of [Spring AI](https://docs.spring.io/spring-ai/reference/) with the [Tavily Search API](https://tavily.com/) to build a conversational AI application using the [Vaadin](https://vaadin.com/) framework for the user interface.

The application allows users to input natural language queries. These queries are then processed through a chain of AI agents:

1. **Query Formulation Agent:** Transforms the user's input into a structured `TavilyRequest` object, optimizing it for the Tavily Search API.
2. **Tavily Search:** The `TavilyApiClient` sends the structured query to the Tavily API, which performs a search and returns relevant results.
3. **Response Curation Agent:** Takes the raw search results and crafts a well-structured, conversational response with a detailed summary and references to the most relevant sources, using markdown format for links.

The application's user interface is built with Vaadin and features a simple chat-like interface where users can interact with the AI.

## Prerequisites

*   Java 17 or higher
*   Maven or Gradle (this project uses Gradle)
*   OpenAI API Key - set as environment variable `OPENAI_API_KEY`
*   Tavily API Key - set as environment variable `TAVILY_API_KEY`

## Getting Started

1. **Set environment variables:**

    ```bash
    export OPENAI_API_KEY=<your-openai-api-key>
    export TAVILY_API_KEY=<your-tavily-api-key>
    ```

2. **Build and run the application (using Gradle):**

    ```bash
    ./gradlew bootRun
    ```

3. **Access the application:**

   Open your web browser and navigate to `http://localhost:8080`.

## Project Structure

The project is organized as follows:

*   `src/main/java/com/apappascs/spring/ai/tavily`:
    *   `agent`: Contains the `QueryFormulationAgent` and `ResponseCurationAgent` that process user input and search results.
    *   `service`: Contains the `TavilyApiClient` for interacting with the Tavily API and the `ChatService` that orchestrates the AI agents.
    *   `MainView.java`: The Vaadin-based user interface.

## Technologies Used

*   [Spring AI](https://docs.spring.io/spring-ai/reference/): A framework for building AI-powered applications.
*   [Tavily Search API](https://tavily.com/): An API for performing high-quality, real-time searches.
*   [Vaadin](https://vaadin.com/): A web framework for building user interfaces in Java.
*   [Spring Boot](https://spring.io/projects/spring-boot): A framework for building stand-alone, production-grade Spring applications.
*   [OpenAI](https://openai.com/): Large Language Models (LLMs)
*   [Gradle](https://gradle.org/): A build automation tool.
*   [Project Lombok](https://projectlombok.org/): A library to reduce boilerplate code.
*   [Viritin](https://vaadin.com/directory/component/viritin): Useful additions to Vaadin.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the project.