package com.productbot;

import com.productbot.client.MessengerClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BotStart {

	private final MessengerClient messengerClient;

	public BotStart(MessengerClient messengerClient) {
		this.messengerClient = messengerClient;
	}

	@Component
	class ContextRefresh implements ApplicationListener<ApplicationReadyEvent> {

		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {
			messengerClient.setWebHooks();
		}
	}

}
