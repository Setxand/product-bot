package com.productbot.service;

import com.messanger.Messaging;
import com.messanger.UserData;
import com.productbot.model.MessengerUser;
import com.productbot.model.Role;
import com.productbot.repository.UserRepository;

public interface PostbackParser {

	default UserData createUser(UserData userData, Long id) {
		UserRepository userRepo = getUserRepo();
		userRepo.findById(id).orElseGet(() -> {

			MessengerUser messengerUser = new MessengerUser();
			messengerUser.setId(id);
			messengerUser.setFirstName(userData.getFirstName());
			messengerUser.setLastName(userData.getLastName());
			messengerUser.setLocale(userData.getLocale());
			messengerUser.setRole(Role.USER);
			userRepo.save(messengerUser);
			return null;
		});

		return userData;
	}

	UserRepository getUserRepo();

	void getStarted(Messaging messaging);

}
