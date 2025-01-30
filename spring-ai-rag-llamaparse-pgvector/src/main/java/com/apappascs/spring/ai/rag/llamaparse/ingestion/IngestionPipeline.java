package com.apappascs.spring.ai.rag.llamaparse.ingestion;

import java.util.List;

import com.apappascs.spring.ai.rag.llamaparse.service.LlamaParseApiClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IngestionPipeline {

	private static final int MAX_ATTEMPTS = 60; // 60 attempts * 5 seconds = 5 minutes
	private static final long POLL_INTERVAL = 5000; // 5 seconds

	private final VectorStore vectorStore;
	private final LlamaParseApiClient llamaParseApiClient;

	@Value("classpath:/documents/Chain_of_Agents_-_Large_Language_Models_Collaborating_on_Long-Context_Tasks.pdf")
	private Resource chainOfAgentsPdf;

	@Value("classpath:/documents/DeepSeekMoE_-_Towards_Ultimate_Expert_Specialization_in_Mixture-of-Experts_Language_Models.pdf")
	private Resource deepSeekMoEPdf;

	@Value("classpath:/documents/Tree_of_Thoughts-_Deliberate_Problem_Solving_with_Large_Language_Models.pdf")
	private Resource treeOfThoughtsPdf;

	@PostConstruct
	void run() {
		// Process PDFs with LlamaParse
		processAndStorePdf(chainOfAgentsPdf, "Chain_of_Agents.pdf");
		processAndStorePdf(deepSeekMoEPdf, "DeepSeekMoE.pdf");
		processAndStorePdf(treeOfThoughtsPdf, "Tree_of_Thoughts.pdf");
	}

	private void processAndStorePdf(Resource pdfResource, String filename) {
		try {
			log.info("Sending PDF to LlamaParse API: {}", pdfResource.getFilename());

			// Prepare the upload request
			LlamaParseApiClient.UploadRequest uploadRequest = LlamaParseApiClient.UploadRequest.builder()
					.file(pdfResource.getContentAsByteArray())
					.filename(filename)
					.autoMode(true)
					.build();

			// Upload the PDF to LlamaParse
			LlamaParseApiClient.UploadResponse uploadResponse = llamaParseApiClient.uploadFile(uploadRequest);

			// Wait for the job to complete (with retries and recovery)
			String jobId = uploadResponse.getId();
			LlamaParseApiClient.MarkdownResultResponse markdownResult = waitForJobCompletion(jobId, filename);

			// Create a Document from the markdown content and add metadata
			Document pdfDocument = new Document(markdownResult.getMarkdown());
			pdfDocument.getMetadata().put("source", pdfResource.getFilename());
			pdfDocument.getMetadata().put("filename", filename);

			// Split the document into chunks and store them in the vector store
			log.info("Splitting and storing document: {}", pdfDocument);
			vectorStore.add(new TokenTextSplitter().split(List.of(pdfDocument)));
			log.info("Document added to vector store: {}", pdfDocument);

		}
		catch (Exception e) {
			log.error("Error processing PDF: {}", pdfResource.getFilename(), e);
		}
	}


	public LlamaParseApiClient.MarkdownResultResponse waitForJobCompletion(String jobId, String filename) {
		log.info("Checking job status for job ID: {}. (Filename: {})", jobId, filename);

		int attempt = 0;

		while (attempt < MAX_ATTEMPTS) {
			LlamaParseApiClient.JobStatusResponse jobStatusResponse = llamaParseApiClient.getJobStatus(jobId);
			LlamaParseApiClient.StatusEnum status = jobStatusResponse.getStatus();

			log.info("Attempt {} - Job status: {}", attempt + 1, status);

			switch (status) {
				case SUCCESS:
					log.info("Job completed successfully. Retrieving markdown result for job ID: {}", jobId);
					return llamaParseApiClient.getMarkdownResult(jobId);
				case PENDING:
					log.info("Job still pending, waiting before retrying...");
					break; // Continue loop
				case ERROR:
					log.error("Job failed with status: ERROR for job ID: {}", jobId);
					return new LlamaParseApiClient.MarkdownResultResponse("Job failed with status: ERROR", new LlamaParseApiClient.JobMetadata());
				case PARTIAL_SUCCESS:
					log.warn("Job partially succeeded for job ID: {}", jobId);
					return new LlamaParseApiClient.MarkdownResultResponse("Job partially succeeded, results may be incomplete.", new LlamaParseApiClient.JobMetadata());
				case CANCELLED:
					log.error("Job was cancelled for job ID: {}", jobId);
					return new LlamaParseApiClient.MarkdownResultResponse("Job was cancelled.", new LlamaParseApiClient.JobMetadata());
			}

			attempt++;

			try {
				Thread.sleep(POLL_INTERVAL);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("Polling was interrupted", e);
				return new LlamaParseApiClient.MarkdownResultResponse("Polling interrupted.", new LlamaParseApiClient.JobMetadata());
			}
		}

		log.error("Job did not complete within {} attempts ({} seconds).", MAX_ATTEMPTS, MAX_ATTEMPTS * (POLL_INTERVAL / 1000));
		return new LlamaParseApiClient.MarkdownResultResponse("Job did not complete within expected time.", new LlamaParseApiClient.JobMetadata());
	}

}