package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.repository.UserRepository;
import com.productbot.service.ProductService;
import com.productbot.service.QuickReplyParser;
import com.productbot.utils.QuickReplyUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

@Service
public class CurtainQuickReplyParser implements QuickReplyParser {

	public enum QuickReplyPayload {

		PREV_Q_PAYLOAD,
		NEXT_Q_PAYLOAD,
		COMMON_Q_PAYLOAD,
		STOP_Q_PAYLOAD
	}

	private final UserRepository userRepo;
	private final ProductService productService;
	private final CurtainMessengerClient messengerClient;


	public CurtainQuickReplyParser(UserRepository userRepo, ProductService productService,
								   CurtainMessengerClient messengerClient) {
		this.userRepo = userRepo;
		this.productService = productService;
		this.messengerClient = messengerClient;
	}

	@Override
	public UserRepository getUserRepo() {
		return userRepo;
	}

	public void commonPayload(Messaging messaging) {
		MessengerUser user = userRepo.getOne(messaging.getSender().getId());

		if (user.getStatus() == MessengerUser.UserStatus.CREATE_PROD3) {
			productService.addProdFilling(messaging);

			int firstEl = Integer.parseInt(QuickReplyUtils
								 .getParams(messaging.getMessage().getQuickReply().getPayload())[1]);

			String text = ResourceBundle.getBundle("dialog").getString(user.getStatus().name());
			messengerClient.sendFillingsAsQuickReplies(text, messaging, productService
											.getProductFillings(PageRequest.of(firstEl, 8)), firstEl);
		}

	}

	public void nextPayload(Messaging messaging) {
		MessengerUser user = userRepo.getOne(messaging.getSender().getId());

		if (user.getStatus() == MessengerUser.UserStatus.CREATE_PROD3) {
			String text = ResourceBundle.getBundle("dialog").getString(user.getStatus().name());

			int firstEl = Integer.parseInt(QuickReplyUtils
					.getParams(messaging.getMessage().getQuickReply().getPayload())[0]) + 1;

			messengerClient.sendFillingsAsQuickReplies(text, messaging, productService
									.getProductFillings(PageRequest.of(firstEl, 8)), firstEl);
		}
	}
}
