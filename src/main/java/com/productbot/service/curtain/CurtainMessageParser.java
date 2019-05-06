package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.ProductFilling;
import com.productbot.repository.FillingRepository;
import com.productbot.repository.UserRepository;
import com.productbot.service.MessageParser;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

@Service
public class CurtainMessageParser implements MessageParser {

	private final CurtainMessengerClient messengerClient;
	private final UserRepository userRepo;
	private final FillingRepository fillingRepo;

	public CurtainMessageParser(CurtainMessengerClient messengerClient, UserRepository userRepo,
								FillingRepository fillingRepo) {
		this.messengerClient = messengerClient;
		this.userRepo = userRepo;
		this.fillingRepo = fillingRepo;
	}

	@Transactional
	public void messageByStatus(Messaging messaging) {
		MessengerUser user = userRepo.getOne(messaging.getSender().getId());
		if (user.getStatus() == null) botEx(messaging);

		switch (user.getStatus()) {

			case CREATE_FILLING1:
				createFilling1(messaging);
				break;

			default:
				botEx(messaging);
		}

	}

	private void botEx(Messaging messaging) {
		throw new BotException(messaging);
	}

	private void createFilling1(Messaging messaging) {
		try {
			String[] fillingText = messaging.getMessage().getText().split(",");

			ProductFilling filling = new ProductFilling();
			filling.setName(fillingText[0]);
			filling.setPrice(Float.valueOf(fillingText[1]));
			fillingRepo.saveAndFlush(filling);
			MessengerUser user = userRepo.getOne(messaging.getSender().getId());
			user.setStatus(null);
			messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog").getString("DONE"), messaging);

		} catch (Exception ex) {
			throw new BotException(messaging, "Try again");
		}
	}

}
