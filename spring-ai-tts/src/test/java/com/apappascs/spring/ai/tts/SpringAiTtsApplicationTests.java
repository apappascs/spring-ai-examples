package com.apappascs.spring.ai.tts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("openai")
@TestPropertySource(properties = {
		"spring.ai.model.audio.speech=openai",
		"spring.ai.openai.api-key=test-key"
})
class SpringAiTtsApplicationTests {

	@Test
	void contextLoads() {
	}

}
