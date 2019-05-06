package com.productbot.client.common;

import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import org.springframework.stereotype.Component;

@Component
public class CommonMessengerClient extends MessengerClient {

	public CommonMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}
}
