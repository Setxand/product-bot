package com.productbot.client.curtain;

import com.messanger.Button;
import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import com.productbot.service.Payload;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CurtainMessengerClient extends MessengerClient {

	public CurtainMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}

	public void navigation(Messaging messaging) {
		List<Button> buttons = Collections.singletonList(createPostbackButton("Create filling",
																		Payload.CT_FILLING_PAYLOAD.name()));
		sendButtons(buttons, "navigation", messaging, "button", "template");
	}

}
