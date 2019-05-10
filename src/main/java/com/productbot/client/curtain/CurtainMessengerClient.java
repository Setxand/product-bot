package com.productbot.client.curtain;

import com.messanger.Messaging;
import com.messanger.QuickReply;
import com.productbot.client.MessengerClient;
import com.productbot.client.UrlProps;
import com.productbot.model.ProductFilling;
import com.productbot.service.curtain.CurtainQuickReplyParser;
import com.productbot.utils.PayloadUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.productbot.service.curtain.CurtainPostbackParser.CurtainPayload.CT_FILLING_PAYLOAD;
import static com.productbot.service.curtain.CurtainPostbackParser.CurtainPayload.CT_PRODUCT_PAYLOAD;
import static com.productbot.service.curtain.CurtainQuickReplyParser.QuickReplyPayload.COMMON_Q_PAYLOAD;
import static java.util.stream.Collectors.toList;

@Component
public class CurtainMessengerClient extends MessengerClient {

	public CurtainMessengerClient(UrlProps urlProps) {
		super(urlProps);
	}

	public void navigation(Messaging messaging) {
		sendPostbackButtons(messaging, "Navigation:", getPButton("Create filling",
				CT_FILLING_PAYLOAD.name()), getPButton("Create product", CT_PRODUCT_PAYLOAD.name()));
	}

	public void sendFillingsAsQuickReplies(String text, Messaging messaging,
										   Page<ProductFilling> fillingList, int firstEl) {

		List<QuickReply> list = fillingList.stream()
				.map(f -> new QuickReply(f.getName(), PayloadUtils.createPayloadWithParams(COMMON_Q_PAYLOAD.name(),
						String.valueOf(f.getId()), String.valueOf(firstEl)))).collect(toList());

		additionalButtons(list, firstEl, fillingList);
		sendQuickReplies(text, messaging, list.toArray(new QuickReply[0]));
	}

	private void additionalButtons(List<QuickReply> list, int firstEl, Page<ProductFilling> fillingList) {

		list.add(new QuickReply("That`s it", CurtainQuickReplyParser.QuickReplyPayload.STOP_Q_PAYLOAD.name()));

		if (firstEl > 0) {

			list.add(0, new QuickReply(" <- previous",
					PayloadUtils.createPayloadWithParams(CurtainQuickReplyParser.QuickReplyPayload
							.PREV_Q_PAYLOAD.name(), String.valueOf(firstEl))));
		}

		if (firstEl < fillingList.getTotalPages() - 1) {
			list.add(new QuickReply("next -> ", PayloadUtils.createPayloadWithParams(CurtainQuickReplyParser.
					QuickReplyPayload.NEXT_Q_PAYLOAD.name(), String.valueOf(firstEl))));
		}
	}
}
