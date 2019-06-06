package com.productbot.client.common;

import com.messanger.Button;
import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import com.productbot.model.Role;
import org.springframework.stereotype.Component;

@Component
public class CommonMessengerClient extends MessengerClient {

	public CommonMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}

	public void navigation(Messaging messaging) {
		sendPostbackButtons(messaging, "Navigation", Role.USER.getNavigationButtons().toArray(new Button[0]));
	}
}
