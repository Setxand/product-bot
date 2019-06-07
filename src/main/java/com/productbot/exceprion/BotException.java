package com.productbot.exceprion;

import com.messanger.Messaging;
import lombok.Getter;

@Getter
public class BotException extends RuntimeException {

	private Messaging messaging;
	private Object data = "Empty";

	public BotException(Messaging messaging) {
		this.messaging = messaging;
	}

	public BotException(Messaging messaging, String message) {
		super(message);
		this.messaging = messaging;
	}

	public BotException(Messaging messaging, String message, Object data) {
		super(message);
		this.messaging = messaging;
		this.data = data;
	}
}
