# Spring AI ElevenLabs Text-to-Speech Example

This example demonstrates how to use Spring AI to generate speech from text using the ElevenLabs API. It provides both blocking and streaming REST endpoints.

## Overview

The project showcases:
- Configuration of Spring AI for ElevenLabs.
- Implementation of a REST controller with two endpoints:
    - `/tts/elevenlabs`: For blocking text-to-speech requests.
    - `/tts/elevenlabs/stream`: For streaming text-to-speech requests.
- Usage of `ElevenLabsSpeechClient` for both blocking and streaming speech generation.

## Prerequisites

- Java 17+
- Gradle
- ElevenLabs Account and API Key

## Configuration

The project is configured via `src/main/resources/application.properties`.

You **must** set your ElevenLabs API key in this file:

```properties
spring.application.name=spring-ai-elevenlabs-example

# ElevenLabs Configuration
spring.ai.elevenlabs.api-key=YOUR_ELEVENLABS_API_KEY # <<< IMPORTANT: SET YOUR API KEY HERE
# Default voice ID (e.g., Rachel by ElevenLabs: 21m00Tcm4TlvDq8ikWAM)
spring.ai.elevenlabs.speech.options.voice-id=21m00Tcm4TlvDq8ikWAM 
# Optional: Default model, stability, similarity boost, style, use_speaker_boost
# spring.ai.elevenlabs.speech.options.model-id=eleven_multilingual_v2
# spring.ai.elevenlabs.speech.options.stability=0.75
# spring.ai.elevenlabs.speech.options.similarity-boost=0.75
# spring.ai.elevenlabs.speech.options.style=0.0 # Value between 0 and 1
# spring.ai.elevenlabs.speech.options.use-speaker-boost=true

logging.level.org.springframework.ai=DEBUG
logging.level.com.apappascs.spring.ai.elevenlabs=INFO
```

Replace `YOUR_ELEVENLABS_API_KEY` with your actual API key. You can also change the default `voice-id` or other options as needed.

## Running the Example

1.  **Navigate to the project directory:**
    ```bash
    cd spring-ai-elevenlabs
    ```

2.  **Set your ElevenLabs API key in `src/main/resources/application.properties`** (if not done already).

3.  **Run the application:**
    ```bash
    ./gradlew bootRun
    ```
    The application will start on `http://localhost:8080`.

## Testing the Endpoints

You can use `curl` or any API client to test the endpoints.

### Blocking TTS Endpoint

This endpoint takes plain text in the request body and returns the MP3 audio data.

```bash
curl -X POST http://localhost:8080/tts/elevenlabs \
     -H "Content-Type: text/plain" \
     -d "Hello from Spring AI! This is a blocking call." \
     --output blocking_output.mp3
```
This will save the generated speech to `blocking_output.mp3` in your current directory.

### Streaming TTS Endpoint

This endpoint takes plain text in the request body and streams the MP3 audio data back.

```bash
curl -X POST http://localhost:8080/tts/elevenlabs/stream \
     -H "Content-Type: text/plain" \
     -d "Hello from Spring AI! This is a streaming call, listen closely as the audio arrives in chunks." \
     --output streaming_output.mp3
```
This will save the streamed speech to `streaming_output.mp3`. You should be able to play the file as it downloads (with a capable player).

## Project Structure

```
.
├── build.gradle.kts
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── README.md
├── settings.gradle.kts
└── src
    └── main
        ├── java
        │   └── com
        │       └── apappascs
        │           └── spring
        │               └── ai
        │                   └── elevenlabs
        │                       ├── SpringAiElevenLabsApplication.java
        │                       └── controller
        │                           └── SpeechController.java
        └── resources
            └── application.properties
```
