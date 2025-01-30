package com.apappascs.spring.ai.rag.llamaparse.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * Client to interact with the LlamaParse API using Spring's RestClient.
 */
@Component
@Slf4j
public class LlamaParseApiClient {

	private static final String BASE_URL = "https://api.cloud.llamaindex.ai/api/v1/parsing";
	private static final String UPLOAD_ENDPOINT = "/upload";
	private static final String JOB_STATUS_ENDPOINT = "/job/{jobId}";
	private static final String RESULT_ENDPOINT = "/job/{jobId}/result/markdown";

	private final RestClient restClient;

	/**
	 * Constructs the LlamaParseApiClient with a RestClient builder.
	 *
	 * @param restClientBuilder   the RestClient builder
	 * @param llamaCloudApiKey the Llama Cloud API key
	 */
	public LlamaParseApiClient(RestClient.Builder restClientBuilder, @Value("${llama-cloud.api-key}") String llamaCloudApiKey) {
		this.restClient = restClientBuilder
				.baseUrl(BASE_URL)
				.defaultHeaders(headers -> headers.setBearerAuth(llamaCloudApiKey))
				.build();
	}

	/**
	 * Uploads a file to the LlamaParse API for parsing.
	 *
	 * @param request The upload request containing the file and options.
	 * @return The response from the LlamaParse API containing the job ID.
	 */
	public UploadResponse uploadFile(UploadRequest request) {
		log.info("Uploading file to LlamaParse API: {}", request.getFilename());

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

		body.add("file", new NamedByteArrayResource(request.getFile(), request.getFilename()));
		body.add("auto_mode", request.isAutoMode());

		return this.restClient.post()
				.uri(UPLOAD_ENDPOINT)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.body(body)
				.retrieve()
				.body(UploadResponse.class);
	}

	/**
	 * Retrieves the status of a parsing job.
	 *
	 * @param jobId The ID of the parsing job.
	 * @return The response from the LlamaParse API containing the job status.
	 */
	public JobStatusResponse getJobStatus(String jobId) {
		log.info("Retrieving status for job ID: {}", jobId);

		return restClient.get()
				.uri(JOB_STATUS_ENDPOINT, jobId)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(JobStatusResponse.class);
	}

	/**
	 * Retrieves the markdown result of a parsing job.
	 *
	 * @param jobId The ID of the parsing job.
	 * @return The response from the LlamaParse API containing the markdown result.
	 */
	public MarkdownResultResponse getMarkdownResult(String jobId) {
		log.info("Retrieving markdown result for job ID: {}", jobId);

		return restClient.get()
				.uri(RESULT_ENDPOINT, jobId)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(MarkdownResultResponse.class);
	}

	/**
	 * Request object for the file upload endpoint.
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonClassDescription("Request object for the file upload endpoint")
	public static class UploadRequest {

		@JsonProperty("file")
		@JsonPropertyDescription("The file to upload.")
		private byte[] file;

		@JsonProperty("filename")
		@JsonPropertyDescription("The filename.")
		private String filename;

		@JsonProperty("auto_mode")
		@JsonPropertyDescription("Whether to use auto mode. Defaults to true.")
		@Builder.Default
		private boolean autoMode = true;

		public Resource getFileResource() {
			return new ByteArrayResource(this.file);
		}
	}

	/**
	 * Response object for the file upload endpoint.
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonClassDescription("Response object for the file upload endpoint")
	public static class UploadResponse {
		@JsonProperty("id")
		@JsonPropertyDescription("The ID of the parsing job.")
		private String id;

		@JsonProperty("status")
		@JsonPropertyDescription("The status of the parsing job.")
		private StatusEnum status;

		@JsonProperty("error_code")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonPropertyDescription("The error code, if any.")
		private String errorCode;

		@JsonProperty("error_message")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonPropertyDescription("The error message, if any.")
		private String errorMessage;
	}

	/**
	 * Response object for the job status endpoint.
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonClassDescription("Response object for the job status endpoint")
	public static class JobStatusResponse {
		@JsonProperty("id")
		@JsonPropertyDescription("The ID of the parsing job.")
		private String id;

		@JsonProperty("status")
		@JsonPropertyDescription("The status of the parsing job.")
		private StatusEnum status;

		@JsonProperty("error_code")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonPropertyDescription("The error code, if any.")
		private String errorCode;

		@JsonProperty("error_message")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonPropertyDescription("The error message, if any.")
		private String errorMessage;
	}

	/**
	 * Response object for the markdown result endpoint.
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonClassDescription("Response object for the markdown result endpoint")
	public static class MarkdownResultResponse {
		@JsonProperty("markdown")
		@JsonPropertyDescription("The markdown result of the parsing job.")
		private String markdown;

		@JsonProperty("job_metadata")
		@JsonPropertyDescription("Parsing job metadata, including usage.")
		private JobMetadata jobMetadata;
	}

	/**
	 * Enum for representing the status of a job.
	 */
	public enum StatusEnum {
		PENDING,
		SUCCESS,
		ERROR,
		PARTIAL_SUCCESS,
		CANCELLED
	}

	/**
	 * Represents the metadata of a parsing job, including usage.
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonClassDescription("Represents the metadata of a parsing job, including usage")
	public static class JobMetadata {
		@JsonProperty("tokens")
		@JsonPropertyDescription("The number of tokens used for the parsing job.")
		private Integer tokens;

		@JsonProperty("pages")
		@JsonPropertyDescription("The number of pages processed in the parsing job.")
		private Integer pages;

		@JsonProperty("characters")
		@JsonPropertyDescription("The number of characters processed in the parsing job.")
		private Integer characters;
	}

	/**
	 * A ByteArrayResource that overrides the getFilename method to provide a specific filename.
	 * Used when sending a file as part of a multipart request.
	 */
	public static class NamedByteArrayResource extends ByteArrayResource {

		private final String filename;

		public NamedByteArrayResource(byte[] byteArray, String filename) {
			super(byteArray);
			Assert.hasText(filename, "Filename must not be empty");
			this.filename = filename;
		}

		@Override
		public String getFilename() {
			return this.filename;
		}
	}
}