package com.productbot.processor;

import com.messanger.Messaging;

public interface Processor {

	default boolean getStartedPostback(Messaging messaging) {
		if (messaging.getPostback().getPayload().equals("GET_STARTED_PAYLOAD")) {
			getStartedAction(messaging);
			return true;
		}
		return false;
	}

	void passPostback(Messaging messaging);

	void passMessage(Messaging messaging);

	void passQuickReply(Messaging messaging);

	void getStartedAction(Messaging messaging);
}
