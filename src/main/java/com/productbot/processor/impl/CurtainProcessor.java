package com.productbot.processor.impl;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.processor.Processor;
import com.productbot.service.PostbackPayload;
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
			switch (PostbackPayload.valueOf(PayloadUtils.getCommonPayload(payload))) {

				case PRODUCT_ACTION_PAYLOAD:
					postbackParser.productActions(messaging);
					break;

				case NAVI_PAYLOAD:
					postbackParser.navigation(messaging);
					break;

				case FILLING_ACTION_PAYLOAD:
					postbackParser.fillingActions(messaging);
					break;

				case NEXT_PROD_PAYLOAD:
					postbackParser.switchMenu(messaging);
					break;

				case PREV_PROD_PAYLOAD:
					postbackParser.switchMenu(messaging);
					break;

				case SET_ROLE_PAYLOAD:
					postbackParser.setRole(messaging);
					break;

				case ORDERINGS_LIST_PAYLOAD:
					postbackParser.orderingList(messaging);
					break;

				case DELETE_PRODUCT_PAYLOAD:
					postbackParser.deleteProduct(messaging);
					break;

				case GET_ORDER_PAYLOAD:
					postbackParser.getOrder(messaging);
					break;

				default:
					throw new BotException(messaging, "This feature hasn't been provided yet", payload);
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

			case CT_PRODUCT_PAYLOAD:
				curtainQuickReplyParser.createProduct(messaging);
				break;

			case UPDATE_PRODUCT_PAYLOAD:
				curtainQuickReplyParser.updateProduct(messaging);
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

			case SET_ROLE_PAYLOAD:
				curtainQuickReplyParser.setRole(messaging);
				break;

			case QUESTION_PAYLOAD:
				questionPayload(messaging);
				break;

			case CREATE_FILLING_PAYLOAD:
				curtainQuickReplyParser.createFilling(messaging);
				break;

			case DELETE_FILLING_PAYLOAD:
				curtainQuickReplyParser.deleteFilling(messaging);
				break;

			default:
				throw new BotException(messaging, "This feature hasn't been provided yet", payloadWithAgrs);
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

	private void questionPayload(Messaging messaging) {
		String payload = messaging.getMessage().getQuickReply().getPayload();
		String questionContext = PayloadUtils.getParams(payload)[0];

		switch (CurtainQuickReplyParser.QuickReplyPayload.valueOf(questionContext)) {

			case PUBLISH_BUCKET:
				curtainQuickReplyParser.courierAcceptance(messaging, payload);
				break;

			case DELETE_PRODUCT_PAYLOAD:
				curtainQuickReplyParser.deleteProduct(messaging, payload);
				break;

			case GET_ORDER_PAYLOAD:
				curtainQuickReplyParser.getOrder(messaging, payload);
				break;

			default:
				throw new BotException(messaging, "This feature hasn't been provided yet", payload);
		}


	}
}
