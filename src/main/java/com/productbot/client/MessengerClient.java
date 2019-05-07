package com.productbot.client;

import com.messanger.Button;
import com.messanger.Messaging;
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

	@Override
	public void sendSimpleQuestion(Long aLong, String s, String s1, String s2) {
/////todo
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
}
