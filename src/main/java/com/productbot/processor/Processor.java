package com.productbot.processor;

import com.messanger.Messaging;
import com.productbot.service.PostbackPayload;

public interface Processor {

	default boolean getStartedPostback(Messaging messaging) {
		if (messaging.getPostback().getPayload().equals(PostbackPayload.GET_STARTED_PAYLOAD.name())) {
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
