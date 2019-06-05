package com.productbot.client.curtain;

import com.messanger.Messaging;
import com.messanger.QuickReply;
import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import com.productbot.model.Role;
import com.productbot.utils.PayloadUtils;
import org.springframework.stereotype.Component;

import static com.productbot.model.Role.*;
import static com.productbot.service.curtain.CurtainPostbackParser.CurtainPayload.*;

@Component
public class CurtainMessengerClient extends MessengerClient {

	public CurtainMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}

	public void navigation(Messaging messaging) {
		sendPostbackButtons(messaging, "Navigation:", getPButton("Create filling",
				CT_FILLING_PAYLOAD.name()), getPButton("Create product", CT_PRODUCT_PAYLOAD.name()),
//				getPButton("Set role", SET_ROLE_PAYLOAD.name()),todo
				getPButton("Orderings list", ORDERINGS_LIST_PAYLOAD.name()));
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
