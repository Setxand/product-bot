package com.productbot.client;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StripeClient {

	private final String STRIPE_API_KEY;
	private static final Logger log = Logger.getLogger(StripeClient.class);

	public StripeClient(@Value("${stripe.key}") String stripeKey) {
		STRIPE_API_KEY = stripeKey;
	}

	public void createCharge(String stripeToken, Integer price) {
		Stripe.apiKey = STRIPE_API_KEY;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", price);
		params.put("currency", "usd");
		params.put("description", "Example charge");
		params.put("source", stripeToken);

		try {
			Charge.create(params);

		} catch (AuthenticationException | InvalidRequestException | APIConnectionException |
				APIException | CardException e) {
			log.warn("Failed to create charge", e);
		}
	}
}
