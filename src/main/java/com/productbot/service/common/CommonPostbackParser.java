package com.productbot.service.common;

import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.service.ProductBucketService;
import com.productbot.service.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

@Service
public class CommonPostbackParser {

	public enum CommonPayload {

		ORDER_PAYLOAD,
		ADD_PRODUCT_PAYLOAD

	}

	private final MessengerClient messengerClient;
	private final UserService userService;
	private final ProductBucketService productBucketService;

	public CommonPostbackParser(MessengerClient messengerClient, UserService userService,
								ProductBucketService productBucketService) {
		this.messengerClient = messengerClient;
		this.userService = userService;
		this.productBucketService = productBucketService;
	}

	public void getStarted(Messaging messaging) {
		Long id = messaging.getSender().getId();
		String userFirstName = userService.createUser(messengerClient
										  .getFacebookUserInfo(id, messaging.getPlatform()), id).getFirstName();

		messengerClient.helloMessage(userFirstName, messaging);
	}

	@Transactional
	public void startOrder(Messaging messaging) {
		MessengerUser user = userService.setUserStatus(messaging, MessengerUser.UserStatus.ORDERING1);
		productBucketService.makeOrder(messaging, user.getStatus());
		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
														.getString(user.getStatus().name()), messaging);
	}
}
