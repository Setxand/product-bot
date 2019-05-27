package com.productbot.client.curtain;

import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import org.springframework.stereotype.Component;

import static com.productbot.service.curtain.CurtainPostbackParser.CurtainPayload.CT_FILLING_PAYLOAD;
import static com.productbot.service.curtain.CurtainPostbackParser.CurtainPayload.CT_PRODUCT_PAYLOAD;

@Component
public class CurtainMessengerClient extends MessengerClient {

	public CurtainMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}

	public void navigation(Messaging messaging) {
		sendPostbackButtons(messaging, "Navigation:", getPButton("Create filling",
				CT_FILLING_PAYLOAD.name()), getPButton("Create product", CT_PRODUCT_PAYLOAD.name()));
	}
}
