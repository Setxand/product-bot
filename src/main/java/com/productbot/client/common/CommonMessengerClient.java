package com.productbot.client.common;

import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import com.productbot.service.common.CommonPostbackParser;
import org.springframework.stereotype.Component;

@Component
public class CommonMessengerClient extends MessengerClient {

	public CommonMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}

	public void navigation(Messaging messaging) {
		sendPostbackButtons(messaging, "Navigation",
				getPButton("Menu", CommonPostbackParser.CommonPayload.MENU_PAYLOAD.name()),
				getPButton("Create own product", CommonPostbackParser.CommonPayload.CREATE_OWN_PAYLOAD.name()));
	}
}
