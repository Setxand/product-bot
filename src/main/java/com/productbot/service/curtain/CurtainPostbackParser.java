package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.messanger.UserData;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import com.productbot.utils.DtoUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

import static com.productbot.model.MessengerUser.UserStatus.SETTING_ROLE1;

@Service
public class CurtainPostbackParser {

	public enum CurtainPayload {

		CT_FILLING_PAYLOAD,
		CT_PRODUCT_PAYLOAD,
		NAVI_PAYLOAD,
		GET_STARTED_PAYLOAD,
		SET_ROLE_PAYLOAD

	}

	private final CurtainMessengerClient messengerClient;
	private final UserService userService;
	private final ProductService productService;

	public CurtainPostbackParser(CurtainMessengerClient messengerClient,
								 UserService userService, ProductService productService) {
		this.messengerClient = messengerClient;
		this.userService = userService;
		this.productService = productService;
	}

	public void setRole(Messaging messaging) {
		userService.setUserStatus(messaging, SETTING_ROLE1);
		messengerClient.sendSimpleMessage("Enter name of user: ", messaging);
	}

	@Transactional
	public void createProduct(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());

		if (user.getStatus() != null) {
			productService.createProduct(messaging, user.getStatus());
			return;
		}

		userService.setUserStatus(messaging, MessengerUser.UserStatus.CREATE_PROD1);
		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
				.getString(MessengerUser.UserStatus.CREATE_PROD1.name()), messaging);
	}

	public void getStarted(Messaging messaging) {
		UserData userData = DtoUtils.user(userService.createUser(
				messengerClient.getFacebookUserInfo(messaging.getSender().getId(),
						messaging.getPlatform()), messaging.getSender().getId()));

		messengerClient.helloMessage(userData.getFirstName(), messaging);
	}

	public void navigation(Messaging messaging) {
		messengerClient.navigation(messaging);
	}

	@Transactional
	public void createFilling(Messaging messaging) {
		MessengerUser user = userService.setUserStatus(messaging, MessengerUser.UserStatus.CREATE_FILLING1);

		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
				.getString(MessengerUser.UserStatus.CREATE_FILLING1.name()), messaging);
	}
}
