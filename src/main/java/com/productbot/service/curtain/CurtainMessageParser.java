package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

import static com.productbot.model.MessengerUser.UserStatus.*;

@Service
public class CurtainMessageParser {

	private final CurtainMessengerClient messengerClient;
	private final UserService userService;
	private final ProductService productService;

	public CurtainMessageParser(CurtainMessengerClient messengerClient,
								UserService userService, ProductService productService) {
		this.messengerClient = messengerClient;
		this.userService = userService;
		this.productService = productService;
	}

	@Transactional
	public void messageByStatus(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());///todo set user in each function which calls user from database
		if (user.getStatus() == null) throw new BotException(messaging, "The best practice is to use navigation");

		switch (user.getStatus()) {

			case CREATE_FILLING1:
				createFilling1(messaging);
				break;

			case CREATE_PROD1:
				createProd(messaging, CREATE_PROD2);
				break;

			case CREATE_PROD2:
				createProd(messaging, CREATE_PROD3);
				break;

			case CREATE_PROD4:
				createProd(messaging, CREATE_PROD5);
				break;

			default:
				throw new BotException(messaging, "I do not support this kind of messages");
		}

	}

	private void createProd(Messaging messaging, MessengerUser.UserStatus nextStatus) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());
		productService.createProduct(messaging, user.getStatus());
		userService.setUserStatus(messaging, nextStatus);

		String text = ResourceBundle.getBundle("dialog", user.getLocale()).getString(nextStatus.name());
		statusAction(messaging, nextStatus, text);
	}

	private void statusAction(Messaging messaging, MessengerUser.UserStatus nextStatus, String text) {
		if (nextStatus == CREATE_PROD3) {

			messengerClient.sendFillingsAsQuickReplies(text, messaging,
					productService.getProductFillings(PageRequest.of(0, 8)), 0);

		} else if (nextStatus == CREATE_PROD5) {

			userService.setUserStatus(messaging, null);
			productService.productCreated(messaging);
			messengerClient.sendSimpleMessage(text, messaging);
			messengerClient.sendGenericTemplate(productService.getMenuElements(messaging, 0, true), messaging);

		} else
			messengerClient.sendSimpleMessage(text, messaging);
	}

	private void createFilling1(Messaging messaging) {
		try {
			productService.createFilling(messaging);

			MessengerUser user = userService.setUserStatus(messaging, null);

			messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
					.getString("DONE"), messaging);
			messengerClient.sendSimpleMessage(productService.getProductFillingsAsString(), messaging);

		} catch (Exception ex) {
			throw new BotException(messaging, "Try again");
		}
	}

}
