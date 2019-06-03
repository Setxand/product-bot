package com.productbot.service;

import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.model.MessengerUser;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.ResourceBundle;

import static com.productbot.model.MessengerUser.UserStatus.CREATE_PROD3;
import static com.productbot.model.MessengerUser.UserStatus.CREATE_PROD5;

public class PostbackHelper {

	private final ProductService productService;
	private final MessengerClient messengerClient;
	private final UserService userService;

	public PostbackHelper(ProductService productService, MessengerClient messengerClient,
						  UserService userService) {
		this.productService = productService;
		this.messengerClient = messengerClient;
		this.userService = userService;
	}

	public void createProd(Messaging messaging, MessengerUser.UserStatus nextStatus) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());
		productService.createProduct(messaging, user.getStatus());
		userService.setUserStatus(messaging, nextStatus);

		String text = ResourceBundle.getBundle("dialog", user.getLocale()).getString(nextStatus.name());
		statusAction(messaging, nextStatus, text);
	}

	public void setRole(Messaging messaging) {
		String name = messaging.getMessage().getText();
		List<MessengerUser> users = userService.getUsersByName(messaging, name);
//		messengerClient.sendUsersAsQuickReplies();todo
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
}
