package com.apappascs.spring.ai.tavily;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Push
public class SpringAiTavilyExampleApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiTavilyExampleApplication.class, args);
	}

}
