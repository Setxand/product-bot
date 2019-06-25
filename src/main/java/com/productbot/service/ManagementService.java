package com.productbot.service;

import com.messanger.Messaging;
import com.messanger.Sender;
import com.productbot.client.Platform;
import com.productbot.client.StripeClient;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ManagementService {

	private final StripeClient stripeClient;
	private final ProductBucketService bucketService;
	private final UserService userService;

	public ManagementService(StripeClient stripeClient, ProductBucketService bucketService, UserService userService) {
		this.stripeClient = stripeClient;
		this.bucketService = bucketService;
		this.userService = userService;
	}

	@Transactional
	public void createCharge(String stripeToken, Integer price, String userId) {
		stripeClient.createCharge(stripeToken, price);

		Messaging messaging = new Messaging();
		messaging.setSender(new Sender(Long.parseLong(userId)));
		messaging.setPlatform(Platform.COMMON);

		bucketService.closeBucket(messaging);
		userService.setUserStatus(messaging, null);
	}
}
