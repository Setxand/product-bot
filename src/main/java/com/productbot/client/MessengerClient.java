package com.productbot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class MessengerClient extends com.messanger.client.MessengerClient {


	public MessengerClient(@Value("${accessTok}") String accessToken, @Value("${url.server}") String serverUrl,
						   @Value("${url.webhook}") String webhook, @Value("${url.map}") String urlMap) {
		super(accessToken, serverUrl, webhook, urlMap);
	}

	@Override
	public void errorMessage(Long recipient) {
		sendSimpleMessage(ResourceBundle.getBundle("dictionary").getString("ERROR_MESSAGE"), recipient);

	}

	@Override
	public void sendSimpleQuestion(Long aLong, String s, String s1, String s2) {
/////todo
	}

	public void helloMessage(Long recipient, String firstName) {
		sendSimpleMessage(String.format(ResourceBundle.getBundle("dictionary")
								.getString("HELLO_MESSAGE"), firstName), recipient);
	}
}
