package com.productbot.service;

import com.messanger.Messaging;
import com.productbot.model.MessengerUser;
import com.productbot.repository.UserRepository;

public interface Parser {

	default MessengerUser setUserStatus(Messaging messaging, MessengerUser.UserStatus userStatus) {
		MessengerUser user = getUserRepo().getOne(messaging.getSender().getId());
		user.setStatus(userStatus);
		return user;
	}

	UserRepository getUserRepo();
}
