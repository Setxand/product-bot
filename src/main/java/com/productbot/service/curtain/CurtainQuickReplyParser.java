package com.productbot.service.curtain;

import com.messanger.Messaging;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.Role;
import com.productbot.service.CourierService;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import com.productbot.service.curtain.helper.CurtainQuickReplyHelper;
import com.productbot.utils.PayloadUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ResourceBundle;

@Service
public class CurtainQuickReplyParser {

	public enum QuickReplyPayload {

		PREV_Q_PAYLOAD,
		NEXT_Q_PAYLOAD,
		COMMON_Q_PAYLOAD,
		STOP_Q_PAYLOAD,
		QUESTION_PAYLOAD,
		SET_ROLE_PAYLOAD,
		PUBLISH_BUCKET,
		DELETE_PRODUCT_PAYLOAD,
		GET_ORDER_PAYLOAD,///todo set enum to service pac
		CASH_PAYLOAD,
		CARD_PAYLOAD,
		CREATE_FILLING_PAYLOAD,
		DELETE_FILLING_PAYLOAD,
		UPDATE_PRODUCT_PAYLOAD,
		CT_PRODUCT_PAYLOAD,
	}

	private final UserService userService;
	private final ProductService productService;
	private final CurtainMessengerClient messengerClient;
	private final CourierService courierService;
	private final CurtainQuickReplyHelper quickReplyHelper;

	public CurtainQuickReplyParser(UserService userService, ProductService productService,
								   CurtainMessengerClient messengerClient, CourierService courierService) {
		this.userService = userService;
		this.productService = productService;
		this.messengerClient = messengerClient;
		this.courierService = courierService;
		quickReplyHelper = new CurtainQuickReplyHelper(userService, messengerClient, productService);
	}

	@Transactional
	public void createProduct(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());

		if (user.getStatus() != null) {
			productService.createProduct(messaging, user.getStatus());
			return;
		}

		userService.setUserStatus(messaging, MessengerUser.UserStatus.CREATE_PROD1);
		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
				.getString(MessengerUser.UserStatus.CREATE_PROD1.name()), messaging);
	}

	public void updateProduct(Messaging messaging) {
		messengerClient.sendGenericTemplate(productService.getMenuElements(0, ProductService.MenuType.UPDATE),
				messaging);
	}

	@Transactional
	public void commonPayload(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());

		String payload = messaging.getMessage().getQuickReply().getPayload();
		String[] params = PayloadUtils.getParams(payload);

		switch (user.getStatus()) {
			case CREATE_PROD3:
				quickReplyHelper.createProd3(messaging, params, user.getStatus());
				break;

			case SETTING_ROLE1:
				quickReplyHelper.settingRole1(messaging, params, user);
				break;

			case DEL_FILLING:
				quickReplyHelper.delFilling(messaging, params);
				break;

			default:
				throw new BotException(messaging, "This feature is not implemented yet");
		}
	}

	public void swipeButtons(Messaging messaging, boolean next) {////todo staff related to user list on role settings
		MessengerUser user = userService.getUser(messaging.getSender().getId());

		String text = ResourceBundle.getBundle("dialog").getString(user.getStatus().name());

		int firstEl = Integer.parseInt(PayloadUtils
				.getParams(messaging.getMessage().getQuickReply().getPayload())[0]);

		firstEl = next ? firstEl + 1 : firstEl - 1;

		if (user.getStatus() == MessengerUser.UserStatus.DEL_FILLING)
			messengerClient.sendFillingsForDelete(text, messaging, productService
					.getProductFillings(PageRequest.of(firstEl, 8)), firstEl);
		 else
			messengerClient.sendFillingsAsQuickReplies(text, messaging, productService
					.getProductFillings(PageRequest.of(firstEl, 8)), firstEl);
	}

	@Transactional
	public void stopButton(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());
		userService.setUserStatus(messaging, MessengerUser.UserStatus.CREATE_PROD4);
		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
				.getString(MessengerUser.UserStatus.CREATE_PROD4.name()), messaging);
	}

	@Transactional
	public void setRole(Messaging messaging) {
		MessengerUser user = userService.getUser(messaging.getSender().getId());
		MessengerUser contextUser = userService.getUser(Long.parseLong(user.getAdminMetaInfo()));
		user.setAdminMetaInfo(null);

		String role = PayloadUtils.getParams(messaging.getMessage().getQuickReply().getPayload())[0];
		contextUser.setRole(Role.valueOf(role));

		if (Role.valueOf(role) == Role.COURIER) {
			courierService.createCourier(user.getId().toString());
		}

		user.setStatus(null);
		messengerClient.sendSimpleMessage("Done", messaging);
	}

	public void courierAcceptance(Messaging messaging, String payload) {
		String[] params = PayloadUtils.getParams(payload);

		if (quickReplyAnswer(params)) {
			courierService.addProductBucket(messaging, params[1]);
			messengerClient.sendSimpleMessage("Added", messaging);
			courierService.courierAccepted(messaging);
		}

	}

	@Transactional
	public void createFilling(Messaging messaging) {
		MessengerUser user = userService.setUserStatus(messaging, MessengerUser.UserStatus.CREATE_FILLING1);

		messengerClient.sendSimpleMessage(ResourceBundle.getBundle("dialog", user.getLocale())
				.getString(MessengerUser.UserStatus.CREATE_FILLING1.name()), messaging);
	}

	public void deleteProduct(Messaging messaging, String payload) {
		String[] params = PayloadUtils.getParams(payload);

		if (quickReplyAnswer(params)) {
			productService.deleteProduct(params[1]);
			messengerClient.sendSimpleMessage("Done! Check it out:", messaging);
			messengerClient
					.sendGenericTemplate(productService.getMenuElements(0, ProductService.MenuType.UPDATE), messaging);

		} else {
			declined(messaging);
		}
	}

	public void getOrder(Messaging messaging, String payload) {
		String[] params = PayloadUtils.getParams(payload);

		if (quickReplyAnswer(params)) {
			courierService.acceptCourierOrder(messaging);
			messengerClient.sendSimpleMessage("Done", messaging);

		} else {
			declined(messaging);
		}
	}

	@Transactional
	public void deleteFilling(Messaging messaging) {
		userService.setUserStatus(messaging, MessengerUser.UserStatus.DEL_FILLING);
		messengerClient
				.sendFillingsForDelete("Choose filling to delete: ",
						messaging, productService.getProductFillings(PageRequest.of(0, 8)), 0);
	}

	private void declined(Messaging messaging) {
		messengerClient.sendSimpleMessage("Declined", messaging);
	}

	private boolean quickReplyAnswer(String[] params) {
		return Integer.parseInt(params[params.length - 1]) == 1;
	}
}
