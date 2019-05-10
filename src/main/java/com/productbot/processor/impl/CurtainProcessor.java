package com.productbot.processor.impl;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.processor.Processor;
import com.productbot.service.curtain.CurtainMessageParser;
import com.productbot.service.curtain.CurtainPostbackParser;
import com.productbot.service.curtain.CurtainQuickReplyParser;
import com.productbot.utils.PayloadUtils;

public class CurtainProcessor implements Processor {

	private final CurtainPostbackParser postbackParser;
	private final CurtainMessageParser curtainMessageParser;
	private final CurtainQuickReplyParser curtainQuickReplyParser;

	public CurtainProcessor(CurtainPostbackParser postbackParser, CurtainMessageParser curtainMessageParser,
							CurtainQuickReplyParser curtainQuickReplyParser) {
		this.postbackParser = postbackParser;
		this.curtainMessageParser = curtainMessageParser;
		this.curtainQuickReplyParser = curtainQuickReplyParser;
	}

	@Override
	public void passPostback(Messaging messaging) {
		if (!getStartedPostback(messaging)) {
			String payload = messaging.getPostback().getPayload();
			switch (CurtainPostbackParser.CurtainPayload.valueOf(PayloadUtils.getCommonPayload(payload))) {

				case NAVI_PAYLOAD:
					postbackParser.navigation(messaging);
					break;

				case CT_FILLING_PAYLOAD:
					postbackParser.createFilling(messaging);
					break;

				case CT_PRODUCT_PAYLOAD:
					postbackParser.createProduct(messaging);
					break;

				default:
					throw new BotException(messaging);
			}
		}
	}

	@Override
	public void passQuickReply(Messaging messaging) {
		String payloadWithAgrs = messaging.getMessage().getQuickReply().getPayload();
		String commonPayload = PayloadUtils.getCommonPayload(payloadWithAgrs);

		switch (CurtainQuickReplyParser.QuickReplyPayload.valueOf(commonPayload)) {

			case NEXT_Q_PAYLOAD:
				curtainQuickReplyParser.swipeButtons(messaging, true);
				break;

			case COMMON_Q_PAYLOAD:
				curtainQuickReplyParser.commonPayload(messaging);
				break;

			case PREV_Q_PAYLOAD:
				curtainQuickReplyParser.swipeButtons(messaging, false);
				break;

			case STOP_Q_PAYLOAD:
				curtainQuickReplyParser.stopButton(messaging);
				break;

			default:
				throw new BotException(messaging);
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
