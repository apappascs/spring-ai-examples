package com.apappascs.spring.ai.tavily;

import com.apappascs.spring.ai.tavily.service.ChatService;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.messagelist.MarkdownMessage;

import static org.vaadin.firitin.components.messagelist.MarkdownMessage.Color.AVATAR_PRESETS;

@Route("")
@PageTitle("Spring AI Chat")
class MainView extends VerticalLayout {

	VerticalLayout messageList = new VerticalLayout();
	MessageInput messageInput = new MessageInput();
	Scroller messageScroller = new Scroller(messageList);

	public MainView(ChatService chatService) {
		add(messageScroller, messageInput);
		setSizeFull();
		setMargin(false);
		messageScroller.setSizeFull();
		messageInput.setWidthFull();

		messageInput.addSubmitListener(event -> {
			String userInput = event.getValue();

			// Display the user's input
			MarkdownMessage userMessage = new MarkdownMessage(userInput, "You", AVATAR_PRESETS[1]);
			messageList.add(userMessage);
			userMessage.scrollIntoView();

			// Prepare assistant's response container
			MarkdownMessage assistantMessage = new MarkdownMessage("...", "Assistant", AVATAR_PRESETS[2]);
			messageList.add(assistantMessage);
			assistantMessage.scrollIntoView();

			// Generate curated response via ChatService
			chatService.generateCuratedResponse(userInput)
					.subscribe(assistantMessage::appendMarkdownAsync);
			assistantMessage.scrollIntoView();
		});
	}
}
