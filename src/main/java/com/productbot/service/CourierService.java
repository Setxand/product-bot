package com.productbot.service;

import com.messanger.Messaging;
import com.messanger.Sender;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.model.ProductBucket;
import com.productbot.model.Role;
import com.productbot.utils.PayloadUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static com.productbot.client.Platform.CURTAIN;
import static com.productbot.service.curtain.CurtainQuickReplyParser.QuickReplyPayload.PUBLISH_BUCKET;
import static com.productbot.service.curtain.CurtainQuickReplyParser.QuickReplyPayload.QUESTION_PAYLOAD;

@Service
public class CourierService {

	private final UserService userService;
	private final CurtainMessengerClient messengerClient;
	private final ProductService productService;

	public CourierService(UserService userService, CurtainMessengerClient messengerClient,
						  ProductService productService) {
		this.userService = userService;
		this.messengerClient = messengerClient;
		this.productService = productService;
	}

	public void publishBucket(Messaging messaging, ProductBucket bucket) {
		Page<MessengerUser> users = userService.findUsersByRole(Role.COURIER);

		users.stream().filter(u -> u.getRole() == Role.COURIER).forEach(u -> {
			Messaging courierMessaging = new Messaging();

			courierMessaging.setPlatform(CURTAIN);
			courierMessaging.setSender(new Sender(u.getId()));

			messengerClient.sendSimpleMessage(createOrderingString(bucket, u), courierMessaging);

			messengerClient.sendSimpleQuestion(PayloadUtils.createPayloadWithParams(QUESTION_PAYLOAD.name(),
					PUBLISH_BUCKET.name()), courierMessaging, "Accept this order?");//todo acceptance, send it to current user
		});
	}

		private String createOrderingString(ProductBucket bucket, MessengerUser user) {
			return "New Order!!!" +
					"\nname: " + user.getFirstName() + " " + user.getLastName() + "\nphone: " + bucket.getPhone() +
					"\nProducts: " + bucket.getProducts().stream().map(p -> productService.getProduct(p).getName())
					.collect(Collectors.joining(",")) +
					"\n\nlocation: \n" + bucket.getLocation();
		}
}
