package com.productbot.service.common;

import com.messanger.Messaging;
import com.productbot.client.MessengerClient;
import com.productbot.repository.UserRepository;
import com.productbot.service.PostbackParser;
import org.springframework.stereotype.Service;

@Service
public class CommonPostbackParser implements PostbackParser {

	private final MessengerClient messengerClient;
	private final UserRepository userRepo;

	public CommonPostbackParser(MessengerClient messengerClient, UserRepository userRepo) {
		this.messengerClient = messengerClient;
		this.userRepo = userRepo;
	}

	public void getStarted(Messaging messaging) {
		Long id = messaging.getSender().getId();
		String userFirstName = createUser(messengerClient.getFacebookUserInfo(id, messaging.getPlatform()), id)
				.getFirstName();

		messengerClient.helloMessage(userFirstName, messaging);
	}

	@Override
	public UserRepository getUserRepo() {
		return userRepo;
	}
}
