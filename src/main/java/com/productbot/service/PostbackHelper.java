package com.productbot.service;

import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.model.Product;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.ResourceBundle;

import static com.productbot.client.Platform.CURTAIN;
import static com.productbot.model.MessengerUser.UserStatus.CREATE_PROD3;
import static com.productbot.model.MessengerUser.UserStatus.CREATE_PROD5;

public class PostbackHelper {

	private final ProductService productService;
	private final MessengerClient messengerClient;
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;

	public PostbackHelper(ProductService productService, MessengerClient messengerClient, UserService userService,
						  ApplicationEventPublisher eventPublisher) {
		this.productService = productService;
		this.messengerClient = messengerClient;
		this.userService = userService;
		this.eventPublisher = eventPublisher;
	}

	public void createProd(Messaging messaging, MessengerUser user, MessengerUser.UserStatus nextStatus) {
		productService.createProduct(messaging, user.getStatus());
		userService.setUserStatus(messaging, nextStatus);

		String text = ResourceBundle.getBundle("dialog", user.getLocale()).getString(nextStatus.name());
		statusAction(messaging, nextStatus, text);
	}

	public void setRole(Messaging messaging) {
		String name = messaging.getMessage().getText();
		Page<MessengerUser> users = userService.getUsersByNameAndPlatform(messaging, name, CURTAIN.name());
		messengerClient.sendUsersAsQuickReplies(messaging, users);
	}

	private void statusAction(Messaging messaging, MessengerUser.UserStatus nextStatus, String text) {
		if (nextStatus == CREATE_PROD3) {

			messengerClient.sendFillingsAsQuickReplies(text, messaging,
					productService.getProductFillings(PageRequest.of(0, 8)), 0);

		} else if (nextStatus == CREATE_PROD5) {

			userService.setUserStatus(messaging, null);
			Product product = productService.productCreated(messaging);
			messengerClient.sendSimpleMessage(text, messaging);
			messengerClient.sendGenericTemplate(Collections.singletonList(productService
					.getElement(product.getId())), messaging);

		} else
			messengerClient.sendSimpleMessage(text, messaging);
	}
}
