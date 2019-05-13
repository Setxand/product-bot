package com.productbot.service.common;

import com.messanger.Messaging;
import com.productbot.client.common.CommonMessengerClient;
import com.productbot.service.ProductBucketService;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import com.productbot.utils.PayloadUtils;
import org.springframework.stereotype.Service;

@Service
public class CommonQuickReplyParser {

	public enum QuestionContext {
		SOME_ELSE_CONTEXT
	}

	private final CommonMessengerClient messengerClient;
	private final ProductService productService;
	private final UserService userService;
	private final ProductBucketService productBucketService;

	public CommonQuickReplyParser(CommonMessengerClient messengerClient, ProductService productService,
								  UserService userService, ProductBucketService productBucketService) {
		this.messengerClient = messengerClient;
		this.productService = productService;
		this.userService = userService;
		this.productBucketService = productBucketService;
	}

	public void orderSomethingElse(Messaging messaging) {
		int answer = Integer.parseInt(PayloadUtils.getParams(messaging.getMessage().getQuickReply().getPayload())[1]);

		if (answer == 1) {
			messengerClient.sendGenericTemplate(productService.getMenuElements(messaging, 0, true), messaging);

		} else {
			userService.setUserStatus(messaging, null);
			productBucketService.closeBucket(messaging);
			messengerClient.sendSimpleMessage("thanks for your order", messaging);
		}
	}
}
