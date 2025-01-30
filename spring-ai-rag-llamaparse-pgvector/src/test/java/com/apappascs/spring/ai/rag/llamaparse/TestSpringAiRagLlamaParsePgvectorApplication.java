package com.apappascs.spring.ai.rag.llamaparse;

import org.springframework.boot.SpringApplication;

public class TestSpringAiRagLlamaParsePgvectorApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringAiRagLlamaParsePgvectorApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
