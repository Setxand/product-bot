package com.productbot.client;

import com.messanger.Button;
import com.messanger.Messaging;
import com.messanger.QuickReply;
import com.productbot.service.common.CommonPostbackParser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.ResourceBundle;

@Component
public class MessengerClient extends com.messanger.client.MessengerClient {

	public MessengerClient(UrlProps urlProps) {
		super(urlProps.getMap().get("accessTokMap"), urlProps.getMap().get("server"), urlProps.getMap().get("webhook"),
							urlProps.getMap().get("urlMap"));
	}

	@Override
	public void errorMessage(Messaging messaging) {
		sendSimpleMessage(ResourceBundle.getBundle("dictionary").getString("ERROR_MESSAGE"), messaging);
	}

	public void helloMessage(String userFirstName, Messaging messaging) {
		sendSimpleMessage(String.format(ResourceBundle.getBundle("dictionary")
													  .getString("HELLO_MESSAGE"), userFirstName), messaging);
	}

	public Button getPButton(String title, String payload) {
		Button button = new Button();
		button.setTitle(title);
		button.setType("postback");
		button.setPayload(payload);
		return button;
	}

	public void sendPostbackButtons(Messaging messaging, String text, Button... buttons) {
		sendButtons(Arrays.asList(buttons), text, messaging, "button", "template");
	}

	public void sendSimpleQuestion(String payload, Messaging messaging, String text) {
		sendQuickReplies(text, messaging, new QuickReply("Yes", payload + "&1"), new QuickReply("No", payload + "&0"));
	}

	public void navigation(Messaging messaging) {
		sendPostbackButtons(messaging, "Navigation",
				getPButton("Menu", CommonPostbackParser.CommonPayload.MENU_PAYLOAD.name()),
				getPButton("Create own product", CommonPostbackParser.CommonPayload.CREATE_OWN_PAYLOAD.name()));
	}
}
