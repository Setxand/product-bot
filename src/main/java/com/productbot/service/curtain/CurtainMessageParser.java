package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.repository.UserRepository;
import com.productbot.service.MessageParser;
import com.productbot.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

import static com.productbot.model.MessengerUser.UserStatus.CREATE_PROD2;
import static com.productbot.model.MessengerUser.UserStatus.CREATE_PROD3;

@Service
public class CurtainMessageParser implements MessageParser {

	private final CurtainMessengerClient messengerClient;
	private final UserRepository userRepo;
	private final ProductService productService;

	public CurtainMessageParser(CurtainMessengerClient messengerClient, UserRepository userRepo,
								ProductService productService) {
		this.messengerClient = messengerClient;
		this.userRepo = userRepo;
		this.productService = productService;
	}

	@Transactional
	public void messageByStatus(Messaging messaging) {
		MessengerUser user = userRepo.getOne(messaging.getSender().getId());
		if (user.getStatus() == null) botEx(messaging);

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

			default:
				throw new BotException(messaging, "I do not support this kind of messages");
		}

	}

	@Override
	public UserRepository getUserRepo() {
		return userRepo;
	}

	private void createProd(Messaging messaging, MessengerUser.UserStatus nextStatus) {
		MessengerUser user = userRepo.getOne(messaging.getSender().getId());
		productService.createProduct(messaging, user.getStatus());
		setUserStatus(messaging, nextStatus);

		String text = ResourceBundle.getBundle("dialog", user.getLocale()).getString(nextStatus.name());

		if (nextStatus == CREATE_PROD3)
			createProd3(messaging, text);
		else
			messengerClient.sendSimpleMessage(text, messaging);
	}

	private void createProd3(Messaging messaging, String text) {
		messengerClient.sendFillingsAsQuickReplies(text, messaging,
										productService.getProductFillings(PageRequest.of(0, 8)), 0);
	}

	private void botEx(Messaging messaging) {
		throw new BotException(messaging);
	}

	private void createFilling1(Messaging messaging) {
		try {
			productService.createFilling(messaging);

			MessengerUser user = setUserStatus(messaging, null);

			messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
					.getString("DONE"), messaging);
			messengerClient.sendSimpleMessage(productService.getProductFillingsAsString(), messaging);

		} catch (Exception ex) {
			throw new BotException(messaging, "Try again");
		}
	}

}
