# Spring AI Voice Assistant

An interactive voice assistant powered by Spring AI, demonstrating real-time voice conversations with AI using Text-to-Speech (TTS), Speech-to-Text (Transcription), and Chat APIs.

## Overview

This project showcases:
- **Interactive voice conversations** with AI
- **Multi-provider support** via Spring profiles:
  - **OpenAI**: TTS, Transcription, and Chat
  - **ElevenLabs + OpenAI**: Hybrid setup (ElevenLabs for TTS, OpenAI for Transcription and Chat)
- **Real-time audio processing** pipeline: Voice → Transcription → Chat → TTS → Voice response
- **Provider-agnostic code** using Spring AI's unified interfaces
- **Modern Spring-branded UI** with waveform animations and visual feedback
- **Spring AI 1.1.0** with unified TTS and Transcription interfaces

## Features

### Voice Assistant Flow
1. User clicks phone button
2. AI greets: "Hello! How can I help you today?"
3. User speaks (up to 10 seconds)
4. AI transcribes speech to text
5. AI processes request using ChatClient
6. AI converts response to speech
7. AI responds with voice
8. Cycle repeats for continuous conversation

## Prerequisites

- Java 17+
- Gradle
- API keys:
  - **OpenAI API Key** ([Get it here](https://platform.openai.com/account/api-keys)) - Required for all profiles
  - **ElevenLabs API Key** ([Get it here](https://elevenlabs.io/sign-up)) - Optional, for enhanced TTS quality

## Quick Start

### 1. Clone and Navigate

```bash
cd spring-ai-tts
```

### 2. Configure API Keys

**For OpenAI-only setup:**
```bash
export OPENAI_API_KEY=<YOUR_OPENAI_API_KEY>
export SPRING_PROFILES_ACTIVE=openai
```

**For ElevenLabs TTS with OpenAI Transcription/Chat:**
```bash
export OPENAI_API_KEY=<YOUR_OPENAI_API_KEY>
export ELEVEN_LABS_API_KEY=<YOUR_ELEVEN_LABS_API_KEY>
export SPRING_PROFILES_ACTIVE=elevenlabs
```

### 3. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

### 4. Use the Voice Assistant

1. Open `http://localhost:8080` in your browser
2. Click the green phone button
3. Allow microphone access when prompted
4. Listen to the AI greeting
5. Speak your question or request
6. The AI will respond with voice

## Switching Providers

The application supports two profiles without any code changes:

**OpenAI Profile (all-in-one):**
- TTS: OpenAI (gpt-4o-mini-tts)
- Transcription: OpenAI (gpt-4o-mini-transcribe)
- Chat: OpenAI (gpt-4o-mini)

```bash
export SPRING_PROFILES_ACTIVE=openai
./gradlew bootRun
```

**ElevenLabs Profile (hybrid):**
- TTS: ElevenLabs (eleven_turbo_v2_5) - Superior voice quality
- Transcription: OpenAI (gpt-4o-mini-transcribe)
- Chat: OpenAI (gpt-4o-mini)

```bash
export SPRING_PROFILES_ACTIVE=elevenlabs
./gradlew bootRun
```

## Key Features Demonstrated

### 1. Multi-Model Integration
Combines three Spring AI models seamlessly:
- `TranscriptionModel` for speech-to-text
- `ChatClient` for conversation
- `TextToSpeechModel` for text-to-speech

### 2. Provider Flexibility
- Pure OpenAI setup (all models from one provider)
- Hybrid setup (best-of-breed from multiple providers)
- Zero code changes to switch

## Running Tests

```bash
./gradlew test
```

**Note:** Integration tests require valid API keys:

```bash
export OPENAI_API_KEY=<your-key>
export ELEVEN_LABS_API_KEY=<your-key>
./gradlew test
```

## Troubleshooting

### Microphone Not Working
**Error:** Browser doesn't request microphone permission
**Solution:** Ensure you're accessing via `localhost` or HTTPS (not `http://192.168.x.x`)

### API Key Not Set
**Error:** Application fails to start with authentication error
**Solution:** Ensure API key environment variables are set for active profile

### Audio Not Playing
**Error:** Voice assistant connects but no audio plays
**Solution:** Check browser console for errors, verify API keys are valid

### Wrong Profile Active
**Error:** Application starts but voice endpoints fail
**Solution:** Verify `SPRING_PROFILES_ACTIVE` matches your API key configuration

### Port Already in Use
**Error:** Application fails to start on port 8080
**Solution:** Kill process on port 8080 or change port in `application.properties`

```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

This example uses Spring AI's **unified audio interfaces**:

**Unified TTS Interface:**
```java
private final TextToSpeechModel textToSpeechModel;

// Works with OpenAI, ElevenLabs, and future providers
byte[] audio = textToSpeechModel.call("Hello!");
```

**Unified Transcription Interface:**
```java
private final TranscriptionModel transcriptionModel;

// Works with OpenAI, Azure and future providers
AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile);
String text = transcriptionModel.call(prompt).getResult().getOutput();
```

**Fluent Chat Client:**
```java
ChatClient chatClient = ChatClient.builder()
    .defaultSystem("You are a helpful assistant...")
    .build();

String response = chatClient.prompt()
    .user(userInput)
    .call()
    .content();
```

**Benefits:**
- Write portable code across all providers
- Easy to add new providers
- Consistent API
- Type safety

### Rapid Restart (Kill Port, Clean Build, Run)

Use these three commands in order to stop any previous run, rebuild (skipping tests), and start with a profile:

```bash
lsof -ti:8080 | xargs kill -9 2>/dev/null || true
```
```bash
./gradlew clean build -x test
```
```bash
SPRING_PROFILES_ACTIVE=openai ./gradlew bootRun
```

## Learn More

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Spring AI TTS API](https://docs.spring.io/spring-ai/reference/api/audio/speech/speech.html)
- [Spring AI Transcription API](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)
- [Spring AI Chat API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [OpenAI TTS Documentation](https://platform.openai.com/docs/guides/text-to-speech)
- [OpenAI Transcription Documentation](https://platform.openai.com/docs/guides/speech-to-text)
- [ElevenLabs API Documentation](https://elevenlabs.io/docs)
