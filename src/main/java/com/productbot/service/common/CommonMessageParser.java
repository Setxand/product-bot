package com.productbot.service.common;

import com.messanger.Messaging;
import com.productbot.client.common.CommonMessengerClient;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.service.ProductBucketService;
import com.productbot.service.ProductService;
import com.productbot.service.QuickReplyPayload;
import com.productbot.service.UserService;
import com.productbot.utils.PayloadUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

import static com.productbot.model.MessengerUser.UserStatus.ORDERING2;
import static com.productbot.model.MessengerUser.UserStatus.ORDERING3;
import static com.productbot.service.common.CommonQuickReplyParser.QuestionContext.SOME_ELSE_CONTEXT;

@Service
public class CommonMessageParser {

	private final UserService userService;
	private final ProductBucketService productBucketService;
	private final ProductService productService;
	private final CommonMessengerClient messengerClient;

	public CommonMessageParser(UserService userService, ProductBucketService productBucketService,
							   ProductService productService, CommonMessengerClient messengerClient) {
		this.userService = userService;
		this.productBucketService = productBucketService;
		this.productService = productService;
		this.messengerClient = messengerClient;
	}

	@Transactional
	public boolean messageByStatus(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());

		if (user.getStatus() == null) return false;

		switch (user.getStatus()) {

			case ORDERING1:
				ordering1(messaging, user);
				break;

			case ORDERING2:
				ordering2(messaging, user);
				break;

			default:
				throw new BotException(messaging);
		}

		return true;
	}

	public void sendMenu(Messaging messaging) {
		messengerClient.sendGenericTemplate(productService.getMenuElements(0, ProductService.MenuType.ORDER),
				messaging);
	}

	public void messageByText(Messaging messaging) {

		switch (messaging.getMessage().getText()) {

			case "menu":
				sendMenu(messaging);
				break;

			default:
				throw new BotException(messaging, "I do not know this command");
		}

	}

	private void ordering2(Messaging messaging, MessengerUser user) {
		makeOrderAndSetStatus(messaging, user, ORDERING3);

		String payload = PayloadUtils
				.createPayloadWithParams(QuickReplyPayload.QUESTION_PAYLOAD.name(), SOME_ELSE_CONTEXT.name());

		messengerClient.sendSimpleQuestion(payload, messaging, ResourceBundle.getBundle("dialog", user.getLocale())
				.getString(user.getStatus().name()));
	}

	private void makeOrderAndSetStatus(Messaging messaging, MessengerUser user, MessengerUser.UserStatus status) {
		productBucketService.makeOrder(messaging, user.getStatus());
		user.setStatus(status);
	}

	private void ordering1(Messaging messaging, MessengerUser user) {
		makeOrderAndSetStatus(messaging, user, ORDERING2);
		messengerClient.sendTypedQuickReply("Enter your location: ", messaging, "location");
	}
}
