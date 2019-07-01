package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.messanger.QuickReply;
import com.messanger.UserData;
import com.productbot.client.UrlProps;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.service.PostbackPayload;
import com.productbot.service.ProductBucketService;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import com.productbot.utils.DtoUtils;
import com.productbot.utils.PayloadUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.productbot.model.MessengerUser.UserStatus.SETTING_ROLE1;
import static com.productbot.service.curtain.CurtainQuickReplyParser.QuickReplyPayload.QUESTION_PAYLOAD;

@Service
public class CurtainPostbackParser {

	private final CurtainMessengerClient messengerClient;
	private final UserService userService;
	private final ProductService productService;
	private final ProductBucketService productBucketService;
	private final UrlProps urlProps;

	public CurtainPostbackParser(CurtainMessengerClient messengerClient,
								 UserService userService, ProductService productService,
								 ProductBucketService productBucketService, UrlProps urlProps) {
		this.messengerClient = messengerClient;
		this.userService = userService;
		this.productService = productService;
		this.productBucketService = productBucketService;
		this.urlProps = urlProps;
	}

	public void setRole(Messaging messaging) {
		userService.setUserStatus(messaging, SETTING_ROLE1);
		messengerClient.sendSimpleMessage("Enter name of user: ", messaging);
	}

	public void getStarted(Messaging messaging) {
		UserData userData = DtoUtils.user(userService.createUser(
				messengerClient.getFacebookUserInfo(messaging.getSender().getId(),
						messaging.getPlatform()), messaging.getSender().getId(), messaging.getPlatform()));

		messengerClient.helloMessage(userData.getFirstName(), messaging);
	}

	public void navigation(Messaging messaging) {
		messengerClient.navigation(messaging, userService.getUser(messaging.getSender().getId()).getRole());
	}

	@Transactional
	public void fillingActions(Messaging messaging) {
		messengerClient.sendQuickReplies("Filling actions:", messaging,
				new QuickReply("Create filling", CurtainQuickReplyParser.QuickReplyPayload.CREATE_FILLING_PAYLOAD.name()),
				new QuickReply("Delete filling", CurtainQuickReplyParser.QuickReplyPayload.DELETE_FILLING_PAYLOAD.name()));
	}

	public void orderingList(Messaging messaging) {
		messengerClient.sendGenericTemplate(productBucketService.getOrderingList(0), messaging);
	}

	public void switchMenu(Messaging messaging) {
		String basePayload = messaging.getPostback().getPayload();
		String[] params = PayloadUtils.getParams(basePayload);
		String payload = PayloadUtils.getCommonPayload(basePayload);

		int currentPage = Integer.parseInt(params[0]);
		int setPage = PostbackPayload.valueOf(payload) == PostbackPayload.NEXT_PROD_PAYLOAD ?
				currentPage + 1 : currentPage - 1;

		messengerClient.sendGenericTemplate(productService
				.getMenuElements(setPage, ProductService.MenuType.valueOf(params[1])), messaging);
	}

	public void deleteProduct(Messaging messaging) {
		String payload = messaging.getPostback().getPayload();
		String[] params = PayloadUtils.getParams(payload);

		messengerClient.sendSimpleQuestion(PayloadUtils.createPayloadWithParams(QUESTION_PAYLOAD.name(),
				PayloadUtils.getCommonPayload(payload), params[0]), messaging,
				"Are you sure you want to delete this product?");
	}

	public void getOrder(Messaging messaging) {
		String questionPayload = PayloadUtils.reformPayloadForQuestion(messaging.getPostback().getPayload());
		messengerClient.sendSimpleQuestion(questionPayload, messaging, "Are you sure you want to get this order?");
	}

	public void productActions(Messaging messaging) {
		messengerClient.sendQuickReplies("Choose actions:", messaging,
				new QuickReply("Create product", CurtainQuickReplyParser.QuickReplyPayload.CT_PRODUCT_PAYLOAD.name()),
				new QuickReply("Update product", CurtainQuickReplyParser.QuickReplyPayload.UPDATE_PRODUCT_PAYLOAD.name()));
	}
}
