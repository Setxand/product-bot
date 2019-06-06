package com.productbot.service.common;

import com.messanger.Messaging;
import com.productbot.client.common.CommonMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.service.ProductBucketService;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import com.productbot.service.curtain.CurtainQuickReplyParser;
import com.productbot.utils.PayloadUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

import static com.productbot.service.common.CommonQuickReplyParser.QuestionContext.SOME_ELSE_CONTEXT;

@Service
public class CommonPostbackParser {

	private final CommonMessengerClient messengerClient;
	private final UserService userService;
	private final ProductBucketService productBucketService;
	private final ProductService productService;

	public CommonPostbackParser(CommonMessengerClient messengerClient, UserService userService,
								ProductBucketService productBucketService, ProductService productService) {
		this.messengerClient = messengerClient;
		this.userService = userService;
		this.productBucketService = productBucketService;
		this.productService = productService;
	}

	public void getStarted(Messaging messaging) {
		Long id = messaging.getSender().getId();
		String userFirstName = userService.createUser(messengerClient
				.getFacebookUserInfo(id, messaging.getPlatform()), id, messaging.getPlatform()).getFirstName();

		messengerClient.helloMessage(userFirstName, messaging);
	}

	@Transactional
	public void startOrder(Messaging messaging) {
		MessengerUser user = userService.setUserStatus(messaging, MessengerUser.UserStatus.ORDERING1);
		productBucketService.makeOrder(messaging, user.getStatus());

		messengerClient.sendTypedQuickReply("Enter tour phone number: ", messaging, "user_phone_number");
	}

	public void addProduct(Messaging messaging) {
		productBucketService.addProd(messaging);
		String payload = PayloadUtils
				.createPayloadWithParams(CurtainQuickReplyParser.QuickReplyPayload.QUESTION_PAYLOAD.name(),
						SOME_ELSE_CONTEXT.name());

		MessengerUser user = userService.getUser(messaging.getSender().getId());

		messengerClient.sendSimpleQuestion(payload, messaging, ResourceBundle.getBundle("dialog", user.getLocale())
				.getString(user.getStatus().name()));
	}

	public void directMenu(Messaging messaging, boolean isNext) {
		String payload = messaging.getPostback().getPayload();
		int page = Integer.parseInt(PayloadUtils.getParams(payload)[0]);
		page += isNext ? 1  : (-1);
		ProductService.MenuType menuType = ProductService.MenuType.valueOf(PayloadUtils.getParams(payload)[1]);

		messengerClient.sendGenericTemplate(productService.getMenuElements(page, menuType), messaging);
	}

	public void navigation(Messaging messaging) {
		messengerClient.navigation(messaging);
	}

	public void createOwnProduct(Messaging messaging) {
//		postbackHelper.createProd(messaging, CREATE_PROD1);
	}
}
