package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.messanger.UserData;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.repository.UserRepository;
import com.productbot.service.PostbackParser;
import com.productbot.service.ProductService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

@Service
public class CurtainPostbackParser implements PostbackParser {

	public enum Payload {

		CT_FILLING_PAYLOAD,
		CT_PRODUCT_PAYLOAD,
		NAVI_PAYLOAD,
		GET_STARTED_PAYLOAD,

	}

	private final CurtainMessengerClient messengerClient;
	private final UserRepository userRepo;
	private final ProductService productService;

	public CurtainPostbackParser(CurtainMessengerClient messengerClient, UserRepository userRepo,
								 ProductService productService) {
		this.messengerClient = messengerClient;
		this.userRepo = userRepo;
		this.productService = productService;
	}

	@Transactional
	public void createProduct(Messaging messaging) {
		MessengerUser user = userRepo.getOne(messaging.getSender().getId());

		if (user.getStatus() != null) {
			productService.createProduct(messaging, user.getStatus());
			return;
		}

		setUserStatus(messaging, MessengerUser.UserStatus.CREATE_PROD1);
		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
											.getString(MessengerUser.UserStatus.CREATE_PROD1.name()), messaging);
	}

	public void getStarted(Messaging messaging) {
		UserData userData = createUser(messengerClient.getFacebookUserInfo(messaging.getSender().getId(),
				messaging.getPlatform()), messaging.getSender().getId());

		messengerClient.helloMessage(userData.getFirstName(), messaging);
	}

	public void navigation(Messaging messaging) {
		messengerClient.navigation(messaging);
	}

	@Transactional
	public void createFilling(Messaging messaging) {
		MessengerUser user = setUserStatus(messaging, MessengerUser.UserStatus.CREATE_FILLING1);

		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
						.getString(MessengerUser.UserStatus.CREATE_FILLING1.name()), messaging);
	}

	@Override
	public UserRepository getUserRepo() {
		return userRepo;
	}
}
