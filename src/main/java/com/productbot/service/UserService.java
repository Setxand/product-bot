package com.productbot.service;

import com.messanger.Message;
import com.messanger.Messaging;
import com.messanger.Recipient;
import com.messanger.UserData;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.Role;
import com.productbot.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {

	private final UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Transactional
	public MessengerUser setUserStatus(Messaging messaging, MessengerUser.UserStatus userStatus) {
		MessengerUser user = userRepo.getOne(messaging.getSender().getId());
		user.setStatus(userStatus);
		return user;
	}

	public MessengerUser createUser(UserData userData, Long id) {
		return userRepo.findById(id).orElseGet(() -> {

			MessengerUser messengerUser = new MessengerUser();
			messengerUser.setId(id);
			messengerUser.setFirstName(userData.getFirstName());
			messengerUser.setLastName(userData.getLastName());
			messengerUser.setLocale(userData.getLocale());
			messengerUser.setRole(Role.USER);

			return userRepo.save(messengerUser);
		});
	}

	public MessengerUser getUser(Long id) {
		return userRepo.findById(id).orElseThrow(
						() -> new BotException(new Messaging(new Message(), new Recipient(id))));
	}
}
