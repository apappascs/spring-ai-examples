# Spring AI RAG with LlamaParse and PGvector

This project demonstrates a Retrieval-Augmented Generation (RAG) application built with Spring AI that utilizes [LlamaParse](https://github.com/run-llama/llama_parse) for intelligent document parsing and [PGvector](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html) as the vector store for efficient similarity search.

LlamaParse, developed by LlamaIndex, is a GenAI-native document parser designed to extract structured data from complex documents like PDFs, PowerPoints, and Word files. This structured data is then used to enhance the capabilities of downstream language models in RAG or agent-based applications.

## How it Works

1. **Document Ingestion:** The application ingests PDF documents using the LlamaParse API. The `IngestionPipeline` uploads the documents to the LlamaParse service, which parses them and extracts structured data in markdown format. The parsing process is asynchronous, and the pipeline polls the LlamaParse API until the parsing job is completed.

2. **Document Splitting and Storage:** The parsed markdown content is then split into smaller chunks using `TokenTextSplitter` and stored in a PGvector database. Each chunk is represented as a vector embedding, allowing for semantic search.

3. **Retrieval-Augmented Generation:** When a user submits a query through the `/rag/chat` endpoint, the `RagController` uses the `QuestionAnswerAdvisor` to perform a similarity search against the vector store. The most relevant document chunks are retrieved and used to augment the prompt sent to the ChatClient (configured with an LLM, e.g. OpenAI). This enables the LLM to generate a more informed and contextually relevant response based on the retrieved information.

## Prerequisites

*   Java 17 or higher
*   Docker and Docker Compose
*   OpenAI API Key - set as environment variable `OPENAI_API_KEY`
*   Llama Cloud API Key - set as environment variable `LLAMA_CLOUD_API_KEY`

## Getting Started

1. **Set environment variables:**

    ```bash
    export OPENAI_API_KEY=<your-openai-api-key>
    export LLAMA_CLOUD_API_KEY=<your-llama-cloud-api-key>
    ```

2. **Build and run the application:**

    ```bash
    ./gradlew bootRun
    ```

   The application will start a PostgreSQL database with the PGvector extension enabled, as defined in the `compose.yaml` file, and the `IngestionPipeline` will automatically process the PDF documents located in the `src/main/resources/documents` directory.

## API Endpoints

*   `/rag/chat`: POST endpoint to ask questions. The application will use the RAG approach to generate an answer based on the documents ingested into the vector store.

    **Example:**

    ```bash
    curl -X POST -H "Content-Type: application/json" -d "How do Mixture-of-Experts Language Models work in DeepSeekMoE?" http://localhost:8080/rag/chat
    ```

## Technologies Used

*   [Spring AI](https://docs.spring.io/spring-ai/reference/): Framework for building AI-powered applications.
*   [LlamaParse](https://github.com/run-llama/llama_parse): GenAI-native document parser for extracting structured data.
*   [PGvector](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html): Vector database extension for PostgreSQL, used for efficient similarity search.
*   [Spring Boot](https://spring.io/projects/spring-boot): Framework for building stand-alone, production-grade Spring applications.
*   [Gradle](https://gradle.org/): Build automation tool.
*   [Docker Compose](https://docs.spring.io/spring-ai/reference/api/docker-compose.html): Tool for defining and running multi-container Docker applications.
*   [Project Lombok](https://projectlombok.org/): Java library to reduce boilerplate code.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the project.