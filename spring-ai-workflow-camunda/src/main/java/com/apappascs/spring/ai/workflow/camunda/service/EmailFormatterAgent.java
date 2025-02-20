package com.apappascs.spring.ai.workflow.camunda.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.io.IOException;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

@Service("emailAgent")
@Slf4j
public class EmailFormatterAgent implements JavaDelegate {

	@Value("${EMAIL_RECIPIENT}")
	private String recipientEmail;

	@Value("${EMAIL_SENDER}")
	private String senderEmail;

	private final SendGrid sendGrid;

	private final ChatClient chatClient;

	public EmailFormatterAgent(SendGrid sendGrid, ChatClient.Builder chatClientBuilder) {
		this.sendGrid = sendGrid;
		this.chatClient = chatClientBuilder
				.defaultSystem("""
                        You are an AI assistant tasked with converting a blog post into well-formatted HTML suitable for email.
                        Given a plain text blog post, your goal is to format it with HTML tags for structure and styling.
                        Focus on creating a readable and visually appealing email.

                        Use these HTML elements:
                            <h2> for headings
                            <p> for paragraphs
                            <ul> and <li> for bulleted lists

                        **Input:**
                        -   `blogPostContent`: The plain text content of the blog post.

                        **Output:**
                        -   Well-formatted HTML for the blog post, suitable for embedding in an email.
                        **Do not include any code delimiters (e.g., ```html or ```) in the output.**
                        """)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.build();
	}

	@Override
	public void execute(DelegateExecution execution) {
		log.info("EmailFormatterAgent: Formatting blog post for email...");

		String blogPostContent = (String) execution.getVariable("blogPostContent");

		if (blogPostContent == null || blogPostContent.isEmpty()) {
			blogPostContent = "Apologies, but we were unable to generate content for this email.";
		}

		String prompt = String.format("""
                Blog Post Content:
                %s
                """, blogPostContent);

		String emailContent = chatClient.prompt()
				.user(prompt)
				.call()
				.content();

		execution.setVariable("emailContent", emailContent);
		log.info("EmailFormatterAgent: Email formatted.");

		try {
			sendEmail(recipientEmail, "Your New Blog Post!", emailContent);
			log.info("EmailFormatterAgent: Email sent successfully to {}", recipientEmail);
		}
		catch (IOException e) {
			log.error("Error sending email: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to send email", e);
		}
	}

	public void sendEmail(String toEmail, String subject, String htmlContent) throws IOException {
		Email from = new Email(senderEmail);
		Email to = new Email(toEmail);
		Content content = new Content("text/html", htmlContent);
		Mail mail = new Mail(from, subject, to, content);

		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sendGrid.api(request);
			if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
				log.info("Email sent successfully! Status code: {}", response.getStatusCode());
			}
			else {
				log.error("Email sending failed. Status code: {}, Body: {}", response.getStatusCode(), response.getBody());
				throw new IOException("Failed to send email: " + response.getStatusCode() + " - " + response.getBody());
			}
		}
		catch (IOException ex) {
			log.error("Error sending email (SendGrid API call failed): {}", ex.getMessage(), ex);
			throw ex;
		}
	}
}
