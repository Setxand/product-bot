package com.productbot.service;

import com.messanger.Messaging;
import com.messanger.UserData;
import com.productbot.client.MessengerClient;
import com.productbot.model.User;
import com.productbot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PostbackParserService {

	private final MessengerClient messengerClient;
	private final UserRepository userRepo;

	public PostbackParserService(MessengerClient messengerClient, UserRepository userRepo) {
		this.messengerClient = messengerClient;
		this.userRepo = userRepo;
	}

	void getStarted(Messaging messaging) {
		Long id = messaging.getSender().getId();
		UserData userData = createUser(messengerClient.sendFacebookRequest(id, messaging.getPlatform()), id);
		messengerClient.helloMessage(id, userData.getFirstName());
	}

	private UserData createUser(UserData userData, Long id) {
		userRepo.findById(id).orElseGet(() -> {

			User user = new User();
			user.setId(id);
			user.setFirstName(userData.getFirstName());
			user.setLastName(userData.getLastName());
			user.setLocale(userData.getLocale());
			userRepo.save(user);
			return null;
		});

		return userData;
	}

}
