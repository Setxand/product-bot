package com.productbot.service.common;

import com.messanger.Button;
import com.messanger.Messaging;
import com.messanger.QuickReply;
import com.productbot.client.UrlProps;
import com.productbot.client.common.CommonMessengerClient;
import com.productbot.model.ProductBucket;
import com.productbot.service.ProductBucketService;
import com.productbot.service.ProductService;
import com.productbot.service.UserService;
import com.productbot.utils.PayloadUtils;
import org.springframework.stereotype.Service;

import static com.productbot.service.QuickReplyPayload.CARD_PAYLOAD;
import static com.productbot.service.QuickReplyPayload.CASH_PAYLOAD;

@Service
public class CommonQuickReplyParser {

	public enum QuestionContext {
		SOME_ELSE_CONTEXT
	}

	private final CommonMessengerClient messengerClient;
	private final ProductService productService;
	private final UserService userService;
	private final ProductBucketService productBucketService;
	private final UrlProps urlProps;

	public CommonQuickReplyParser(CommonMessengerClient messengerClient, ProductService productService,
								  UserService userService, ProductBucketService productBucketService, UrlProps urlProps) {
		this.messengerClient = messengerClient;
		this.productService = productService;
		this.userService = userService;
		this.productBucketService = productBucketService;
		this.urlProps = urlProps;
	}

	public void orderSomethingElse(Messaging messaging) {
		int answer = Integer.parseInt(PayloadUtils.getParams(messaging.getMessage().getQuickReply().getPayload())[1]);

		if (answer == 1) {
			messengerClient.sendGenericTemplate(productService.getMenuElements(0, ProductService.MenuType.ADD),
					messaging);

		} else {

			productBucketService.setBucketPrice(messaging);
			messengerClient.sendQuickReplies("Choose payment method: ", messaging,
					new QuickReply("Cash", CASH_PAYLOAD.name()),
					new QuickReply("Card", CARD_PAYLOAD.name()));
		}
	}

	public void cashPayment(Messaging messaging) {
		userService.setUserStatus(messaging, null);
		productBucketService.closeBucket(messaging);
		messengerClient.sendSimpleMessage("thanks for your order", messaging);
	}

	public void cardPayment(Messaging messaging) {

		ProductBucket bucket = productBucketService.getBucket(messaging);

		messengerClient.sendPostbackButtons(messaging, "click to pay: ",
				new Button("Pay").urlButton(urlProps.getMap().get("server") +
						String.format("/v1/products/%s/card-payment?userId=%s",
								bucket.getPrice(), bucket.getUserId())).webView());
	}
}
