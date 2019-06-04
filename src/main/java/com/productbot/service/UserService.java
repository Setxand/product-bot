package com.productbot.service;

import com.messanger.*;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.Role;
import com.productbot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	public MessengerUser createUser(UserData userData, Long id, Platform platform) {
		return userRepo.findById(id).orElseGet(() -> {

			MessengerUser messengerUser = new MessengerUser();
			messengerUser.setId(id);
			messengerUser.setFirstName(userData.getFirstName());
			messengerUser.setLastName(userData.getLastName());
			messengerUser.setLocale(userData.getLocale());
			messengerUser.setImage(userData.getPicture());
			messengerUser.setPlatform(platform.name());
			messengerUser.setRole(Role.USER);

			return userRepo.save(messengerUser);
		});
	}

	public MessengerUser getUser(Long id) {
		return userRepo.findById(id).orElseThrow(
				() -> new BotException(new Messaging(new Message(), new Recipient(id))));
	}

	public MessengerUser getUser(Long id, Platform platform) {
		return userRepo.findByIdAndPlatform(id, platform.name()).orElseThrow(
				() -> new BotException(new Messaging(new Message(), new Recipient(id))));
	}

	public Page<MessengerUser> getUsersByNameAndPlatform(Messaging messaging, String name, String platform) {
		String[] fullName = validateUserName(messaging, name);

		return userRepo.findAllByFirstNameAndLastName(fullName[0], fullName.length > 1 ? fullName[1] : null,
				platform, PageRequest.of(0, 8));
	}

	public Page<MessengerUser> findUsersByRole(Role role) {
		return userRepo.findUsersByRole(role, PageRequest.of(0, 50));
	}

	private String[] validateUserName(Messaging messaging, String name) {
		String[] fullName = name.split(" ");

		if (fullName.length > 2) botEx(messaging, "It Needs only name and last name");

		return fullName;
	}

	private void botEx(Messaging messaging, String s) {
		throw new BotException(messaging, s);
	}
}
