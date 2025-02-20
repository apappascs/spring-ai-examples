# Spring AI Content Creation Workflow with Camunda, Tavily, and SendGrid

This project demonstrates a content creation workflow orchestrated by Camunda, leveraging Spring AI, Tavily Search API, and SendGrid. The workflow automates the process of generating a blog post based on a user-defined topic, searching for relevant information, writing the post, formatting it as an HTML email, and sending the email.

## Workflow Overview

The workflow, defined in `content_creation_workflow.bpmn`, consists of the following steps:

1.  **Start Event:** The workflow begins with a start event, triggered manually or by an external system.
2.  **Search Content (Service Task - `searchAgent`):**
    *   Uses Spring AI's `ChatClient` to formulate a structured search query for the Tavily Search API based on a user-provided topic (defaulting to "Spring AI and Camunda").
    *   Calls the Tavily API (via `TavilyApiClient`) to retrieve search results.
    *   Stores the search results in a process variable (`searchResults`).
3.  **Write Blog Post (Service Task - `writerAgent`):**
    *   Uses Spring AI's `ChatClient` with a system prompt designed to instruct the AI to write a concise blog post (under 250 words).
    *   Takes the user topic and search results as input.
    *   Generates the blog post content.
    *   Stores the blog post content in a process variable (`blogPostContent`).
4.  **Convert to Email (Service Task - `emailAgent`):**
    *   Uses Spring AI's `ChatClient` with a system prompt to convert the plain-text blog post into well-formatted HTML, suitable for an email. It uses `<h2>`, `<p>`, `<ul>`, and `<li>` tags.
    *   Takes the blog post content as input.
    *   Generates the HTML email content.
    *   Stores the HTML content in a process variable (`emailContent`).
    *   Calls the SendGrid API (via the `sendEmail` method) to send the formatted email to a predefined recipient.
5.  **End Event:** The workflow concludes after the email has been sent.

## Prerequisites

*   Java 17 or higher.
*   [Camunda](https://docs.camunda.org/get-started/spring-boot/) Spring-boot.
*   [OpenAI](https://platform.openai.com/) API Key (set as environment variable `OPENAI_API_KEY`).
*   [Tavily Search](https://tavily.com/) API Key (set as environment variable `TAVILY_API_KEY`).
*   [SendGrid](https://github.com/sendgrid/sendgrid-java/) API Key (set as environment variable `SENDGRID_API_KEY`).
*   Define two environment variables (`EMAIL_RECIPIENT`, `EMAIL_SENDER`).

## Getting Started

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/apappascs/spring-ai-examples.git
    cd spring-ai-examples/spring-ai-workflow-camunda
    ```

2.  **Set environment variables:**

    ```bash
    export OPENAI_API_KEY=<your-openai-api-key>
    export TAVILY_API_KEY=<your-tavily-api-key>
    export SENDGRID_API_KEY=<your-sendgrid-api-key>
    export EMAIL_RECIPIENT=<recipient-email-address>
    export EMAIL_SENDER=<sender-email-address>
    ```

3.  **Build the application (using Maven):**

    ```bash
    mvn clean install
    ```

5.  **Run the Spring Boot application:**

    ```bash
    mvn spring-boot:run
    ```

6.  **Start a workflow instance:**

    *   You can start a new workflow instance through the Camunda Tasklist, Cockpit, or REST API.
    *   Example using the REST API (replace with your Camunda deployment URL):

        ```bash
        curl -X POST \
          http://localhost:8080/engine-rest/process-definition/key/spring-ai-workflow-camunda-process/start \
          -H 'Content-Type: application/json' \
          -d '{}'
        ```

        This will start a new instance of the workflow, triggering the search, blog post generation, and email sending process.

## Key Components

*   **`content_creation_workflow.bpmn`:** The BPMN 2.0 workflow definition.  This file can be opened and edited with the [Camunda Modeler](https://camunda.com/download/modeler/).
*   **`service/` package:** Contains the Java delegates that implement the workflow's service tasks.
    *   **`SearchAgent.java`:** Formulates the Tavily search query and retrieves search results.
    *   **`WriterAgent.java`:** Generates the blog post content.
    *   **`EmailFormatterAgent.java`:** Converts the blog post to HTML and sends the email via SendGrid.
    *   **`TavilyApiClient.java`:** A client for interacting with the Tavily Search API, handling requests and responses.
*   **`application.yaml`:** Contains configuration for Spring AI (OpenAI), SendGrid, and Tavily API keys, along with Camunda administrator credentials.

## Technologies Used

*   [Spring AI](https://docs.spring.io/spring-ai/reference/): For interacting with OpenAI's language models.
*   [Camunda](https://camunda.com/platform/): For workflow orchestration.
*   [Tavily Search API](https://tavily.com/): An API for performing high-quality, real-time searches.
*   [Twilio SendGrid](https://sendgrid.com/): For sending emails.
*   [Spring Boot](https://spring.io/projects/spring-boot): For building the application.
*   [Maven](https://maven.apache.org/): For build automation.

## Notes

*   Remember to correctly set the environment variables mentioned above with the corresponding API keys.
*   The prompts used by the `ChatClient` in each service task can be further refined to improve the quality of the generated content.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the project.