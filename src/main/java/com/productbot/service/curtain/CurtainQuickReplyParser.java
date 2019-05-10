package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import com.productbot.utils.PayloadUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

@Service
public class CurtainQuickReplyParser {

	public enum QuickReplyPayload {

		PREV_Q_PAYLOAD,
		NEXT_Q_PAYLOAD,
		COMMON_Q_PAYLOAD,
		STOP_Q_PAYLOAD,
		QUESTION_PAYLOAD
	}

	private final UserService userService;
	private final ProductService productService;
	private final CurtainMessengerClient messengerClient;


	public CurtainQuickReplyParser(UserService userService, ProductService productService,
								   CurtainMessengerClient messengerClient) {
		this.userService = userService;
		this.productService = productService;
		this.messengerClient = messengerClient;
	}

	public void commonPayload(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());

		if (user.getStatus() == MessengerUser.UserStatus.CREATE_PROD3) {
			productService.addProdFilling(messaging);

			int firstEl = Integer.parseInt(PayloadUtils
								 .getParams(messaging.getMessage().getQuickReply().getPayload())[1]);

			String text = ResourceBundle.getBundle("dialog").getString(user.getStatus().name());
			messengerClient.sendFillingsAsQuickReplies(text, messaging, productService
											.getProductFillings(PageRequest.of(firstEl, 8)), firstEl);
		}

	}

	public void swipeButtons(Messaging messaging, boolean next) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());

		String text = ResourceBundle.getBundle("dialog").getString(user.getStatus().name());

		int firstEl = Integer.parseInt(PayloadUtils
				.getParams(messaging.getMessage().getQuickReply().getPayload())[0]);

		firstEl = next ? firstEl + 1 : firstEl - 1;

		messengerClient.sendFillingsAsQuickReplies(text, messaging, productService
				.getProductFillings(PageRequest.of(firstEl, 8)), firstEl);
	}

	@Transactional
	public void stopButton(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());
		userService.setUserStatus(messaging, MessengerUser.UserStatus.CREATE_PROD4);
		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
										  .getString(MessengerUser.UserStatus.CREATE_PROD4.name()), messaging);
	}
}
