package com.productbot.client.curtain;

import com.messanger.Button;
import com.messanger.Messaging;
import com.messanger.QuickReply;
import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import com.productbot.model.Role;
import com.productbot.utils.PayloadUtils;
import org.springframework.stereotype.Component;

import static com.productbot.model.Role.*;
import static com.productbot.service.PostbackPayload.SET_ROLE_PAYLOAD;

@Component
public class CurtainMessengerClient extends MessengerClient {

	public CurtainMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}

	public void navigation(Messaging messaging, Role role) {
		sendPostbackButtons(messaging, "Navigation:", role.getNavigationButtons().toArray(new Button[0]));
	}

	public void sendRoleQuickReplies(String text, Messaging messaging) {
		sendQuickReplies(text, messaging, new QuickReply("User", rolePayload(USER)),
				new QuickReply("Admin", rolePayload(ADMIN)),
				new QuickReply("Courier", rolePayload(COURIER)),
				new QuickReply("Personal", rolePayload(PERSONAL)));
	}

	private String rolePayload(Role role) {
		return PayloadUtils.createPayloadWithParams(SET_ROLE_PAYLOAD.name(), role.name());
	}
}
