package com.productbot.processor.impl;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.processor.Processor;
import com.productbot.service.common.CommonPostbackParser;


public class CommonProcessor implements Processor {


	private final CommonPostbackParser postbackParser;

	public CommonProcessor(CommonPostbackParser postbackParser) {
		this.postbackParser = postbackParser;
	}

	@Override
	public void passPostback(Messaging messaging) {
		getStartedPostback(messaging);

		switch (messaging.getPostback().getPayload()) {
			default:
				throw new BotException(messaging, "This postback have not been implemented for now");
		}
	}

	@Override
	public void passMessage(Messaging messaging) {
		throw new BotException(messaging, "Messages have not been implemented for now");
	}

	@Override
	public void getStartedAction(Messaging messaging) {
		postbackParser.getStarted(messaging);
	}
}
