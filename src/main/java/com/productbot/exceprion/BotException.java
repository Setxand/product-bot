package com.productbot.exceprion;

import com.messanger.Messaging;

public class BotException extends RuntimeException {

	private Messaging messaging;

	public BotException(Messaging messaging) {
		this.messaging = messaging;
	}

	public BotException(Messaging messaging, String message) {
		super(message);
		this.messaging = messaging;
	}

	public Messaging getMessaging() {
		return messaging;
	}
}
