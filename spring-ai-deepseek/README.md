# Spring AI DeepSeek Example

This example demonstrates how to use the DeepSeek Chat model through Spring AI's OpenAI abstraction with tool calling capabilities.

## Overview

The project showcases:
- Configuration of Spring AI to use DeepSeek's API through the OpenAI client adapter
- Implementation of tool calling with a simple date/time tool
- Exposing the model through REST endpoints with both standard and streaming responses

## Prerequisites

- Java 17+
- Gradle
- DeepSeek API key (set as environment variable `DEEPSEEK_API_KEY`)

## Configuration

The project configures DeepSeek by:
1. Using the Spring AI OpenAI starter
2. Setting the base URL to DeepSeek's API
3. Disabling embeddings (not supported by DeepSeek)
4. Configuring the model and temperature

See `application.properties` for details:

```properties
spring.application.name=Spring AI DeepSeek Example
spring.ai.openai.api-key=${DEEPSEEK_API_KEY}
spring.ai.openai.base-url=https://api.deepseek.com
spring.ai.openai.chat.options.model=deepseek-chat
spring.ai.openai.chat.options.temperature=0.7

# The DeepSeek API doesn't support embeddings, so we need to disable it
spring.ai.openai.embedding.enabled=false
```

## Running the Example

1. Set your DeepSeek API key:
   ```bash
   export DEEPSEEK_API_KEY=your-api-key
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```

3. Use the API:
   
   For regular chat completion:
   ```bash
   curl -X POST -H "Content-Type: application/json" \
     -d "What day is tomorrow?" \
     http://localhost:8080/api/deepseek/chat
   ```
   
   For streaming response:
   ```bash
   curl -X POST -H "Content-Type: application/json" \
     -d "What day is tomorrow?" \
     http://localhost:8080/api/deepseek/chat/stream
   ```

## How It Works

1. The client sends a prompt to one of the endpoints:
   - `/api/deepseek/chat` for standard response
   - `/api/deepseek/chat/stream` for streaming response
2. The controller builds a ChatClient with the DateTimeTools
3. The model may invoke the getCurrentDateTime() tool if needed
4. The response is returned to the user (either as a complete response or as a stream)

## Tool Calling

The example includes a simple tool to get the current date and time:

```java
@Tool(description = "Get the current date and time in the user's timezone")
public String getCurrentDateTime() {
    return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
}
```

When the model determines it needs time information, it will invoke this tool via Spring AI's tool calling mechanism.

## Project Structure

```
└── src/
    ├── main/
    │   ├── resources/
    │   │   └── application.properties
    │   └── java/
    │       └── com/
    │           └── apappascs/
    │               └── spring/
    │                   └── ai/
    │                       └── deepseek/
    │                           ├── tools/
    │                           │   └── DateTimeTools.java
    │                           ├── SpringAiDeepseekExampleApplication.java
    │                           └── controller/
    │                               └── DeepSeekController.java
    └── test/ (not shown)
```
