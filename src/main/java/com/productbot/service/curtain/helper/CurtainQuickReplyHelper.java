package com.productbot.service.curtain.helper;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import org.springframework.data.domain.PageRequest;

import java.util.ResourceBundle;

public class CurtainQuickReplyHelper {

	private final UserService userService;
	private final CurtainMessengerClient messengerClient;
	private final ProductService productService;

	public CurtainQuickReplyHelper(UserService userService, CurtainMessengerClient messengerClient,
								   ProductService productService) {
		this.userService = userService;
		this.messengerClient = messengerClient;
		this.productService = productService;
	}

	public void delFilling(Messaging messaging, String[] params) {
		String fillingId = params[0];
		productService.deleteFilling(fillingId);
		messengerClient.sendSimpleMessage("Done", messaging);
		userService.setUserStatus(messaging, null);
	}

	public void settingRole1(Messaging messaging, String[] params, MessengerUser user) {
		String contextId = params[0];
		MessengerUser contextUser = userService.getUser(Long.parseLong(contextId));
		user.setAdminMetaInfo(contextUser.getId().toString());

		messengerClient.sendRoleQuickReplies("Enter role for " + contextUser.getFirstName() + ": ", messaging);
	}

	public void createProd3(Messaging messaging, String[] params, MessengerUser.UserStatus status) {
		productService.addProdFilling(messaging);

		int firstEl = Integer.parseInt(params[1]);

		String text = ResourceBundle.getBundle("dialog").getString(status.name());
		messengerClient.sendFillingsAsQuickReplies(text, messaging, productService
				.getProductFillings(PageRequest.of(firstEl, 8)), firstEl);
	}
}
