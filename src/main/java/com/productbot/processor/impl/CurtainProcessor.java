package com.productbot.processor.impl;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.processor.Processor;
import com.productbot.service.Payload;
import com.productbot.service.curtain.CurtainMessageParser;
import com.productbot.service.curtain.CurtainPostbackParser;

public class CurtainProcessor implements Processor {

	private final CurtainPostbackParser postbackParser;
	private final CurtainMessageParser curtainMessageParser;

	public CurtainProcessor(CurtainPostbackParser postbackParser, CurtainMessageParser curtainMessageParser) {
		this.postbackParser = postbackParser;
		this.curtainMessageParser = curtainMessageParser;
	}

	@Override
	public void passPostback(Messaging messaging) {
		if (!getStartedPostback(messaging)) {
			switch (Payload.valueOf(messaging.getPostback().getPayload())) {

				case NAVI_PAYLOAD:
					postbackParser.navigation(messaging);
					break;

				case CT_FILLING_PAYLOAD:
					postbackParser.createFilling(messaging);
					break;

				default:
					throw new BotException(messaging);
			}
		}
	}

	@Override
	public void passMessage(Messaging messaging) {
		curtainMessageParser.messageByStatus(messaging);
	}

	@Override
	public void getStartedAction(Messaging messaging) {
		postbackParser.getStarted(messaging);
	}
}
