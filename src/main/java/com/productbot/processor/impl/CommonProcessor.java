package com.productbot.processor.impl;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.processor.Processor;
import com.productbot.service.common.CommonMessageParser;
import com.productbot.service.common.CommonPostbackParser;
import com.productbot.service.common.CommonQuickReplyParser;
import com.productbot.service.curtain.CurtainQuickReplyParser;
import com.productbot.utils.PayloadUtils;


public class CommonProcessor implements Processor {


	private final CommonPostbackParser postbackParser;
	private final CommonMessageParser messageParser;
	private final CommonQuickReplyParser quickReplyParser;

	public CommonProcessor(CommonPostbackParser postbackParser, CommonMessageParser messageParser,
						   CommonQuickReplyParser quickReplyParser) {
		this.postbackParser = postbackParser;
		this.messageParser = messageParser;
		this.quickReplyParser = quickReplyParser;
	}

	@Override
	public void passPostback(Messaging messaging) {
		if (!getStartedPostback(messaging)) {
			String payload = messaging.getPostback().getPayload();

			switch (CommonPostbackParser.CommonPayload.valueOf(PayloadUtils.getCommonPayload(payload))) {

				case ORDER_PAYLOAD:
					postbackParser.startOrder(messaging);
					break;

				case ADD_PRODUCT_PAYLOAD:
					postbackParser.addProduct(messaging);
					break;

				case NEXT_PROD_PAYLOAD:
					postbackParser.directMenu(messaging, true);
					break;

				case PREV_PROD_PAYLOAD:
					postbackParser.directMenu(messaging, false);
					break;

				case MENU_PAYLOAD:
					messageParser.sendMenu(messaging);
					break;

				case NAVIGATION_MENU:
					postbackParser.navigation(messaging);
					break;

				case CREATE_OWN_PAYLOAD:
					postbackParser.createOwnProduct(messaging);
					break;

				default:
					throw new BotException(messaging);
			}
		}
	}

	@Override
	public void passMessage(Messaging messaging) {
		if (!messageParser.messageByStatus(messaging))
			messageParser.messageByText(messaging);
	}

	@Override
	public void passQuickReply(Messaging messaging) {
		String payload = messaging.getMessage().getQuickReply().getPayload();

		if (payload.contains("+") && payload.length() == 13)
			passMessage(messaging);
		else
			switch (CurtainQuickReplyParser.QuickReplyPayload.valueOf(PayloadUtils.getCommonPayload(payload))) {

				case QUESTION_PAYLOAD:
					questionPayload(messaging);
					break;

				default:
					throw new BotException(messaging);
			}

	}

	@Override
	public void getStartedAction(Messaging messaging) {
		postbackParser.getStarted(messaging);
	}

	private void questionPayload(Messaging messaging) {

		switch (CommonQuickReplyParser.QuestionContext
				.valueOf(PayloadUtils.getParams(messaging.getMessage().getQuickReply().getPayload())[0])) {

			case SOME_ELSE_CONTEXT:
				quickReplyParser.orderSomethingElse(messaging);
				break;

			default:
				throw new BotException(messaging);
		}
	}
}
