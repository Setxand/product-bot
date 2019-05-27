package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.service.PostbackHelper;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

import static com.productbot.model.MessengerUser.UserStatus.*;

@Service
public class CurtainMessageParser {

	private final CurtainMessengerClient messengerClient;
	private final UserService userService;
	private final ProductService productService;
	private final PostbackHelper postbackHelper;

	public CurtainMessageParser(CurtainMessengerClient messengerClient,
								UserService userService, ProductService productService) {
		this.messengerClient = messengerClient;
		this.userService = userService;
		this.productService = productService;
		postbackHelper = new PostbackHelper(productService, messengerClient, userService);
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
				postbackHelper.createProd(messaging, CREATE_PROD2);
				break;

			case CREATE_PROD2:
				postbackHelper.createProd(messaging, CREATE_PROD3);
				break;

			case CREATE_PROD4:
				postbackHelper.createProd(messaging, CREATE_PROD5);
				break;

			case SETTING_ROLE1:
				postbackHelper.setRole(messaging);
				break;

			default:
				throw new BotException(messaging, "I do not support this kind of messages");
		}

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
