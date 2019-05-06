package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.messanger.UserData;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.repository.UserRepository;
import com.productbot.service.PostbackParser;
import com.productbot.service.UserStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

@Service
public class CurtainPostbackParser implements PostbackParser {

	private final CurtainMessengerClient messengerClient;
	private final UserRepository userRepo;

	public CurtainPostbackParser(CurtainMessengerClient messengerClient, UserRepository userRepo) {
		this.messengerClient = messengerClient;
		this.userRepo = userRepo;
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

		MessengerUser user = userRepo.getOne(messaging.getSender().getId());
		user.setStatus(UserStatus.CREATE_FILLING1);

		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog")
										  .getString(UserStatus.CREATE_FILLING1.name()), messaging);
	}

	@Override
	public UserRepository getUserRepo() {
		return userRepo;
	}

}
