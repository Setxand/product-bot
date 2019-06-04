package com.productbot.client;

import com.messanger.Button;
import com.messanger.Messaging;
import com.messanger.QuickReply;
import com.productbot.model.MessengerUser;
import com.productbot.model.ProductFilling;
import com.productbot.service.curtain.CurtainQuickReplyParser;
import com.productbot.utils.PayloadUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.productbot.service.curtain.CurtainQuickReplyParser.QuickReplyPayload.COMMON_Q_PAYLOAD;
import static java.util.stream.Collectors.toList;

@Component
public class MessengerClient extends com.messanger.client.MessengerClient {

	public enum AdditionalButtonCases {
		USERS,
		FILLINGS
	}

	public MessengerClient(UrlProps urlProps) {
		super(urlProps.getMap().get("accessTokMap"), urlProps.getMap().get("server"), urlProps.getMap().get("webhook"),
				urlProps.getMap().get("urlMap"));
	}

	@Override
	public void errorMessage(Messaging messaging) {
		sendSimpleMessage(ResourceBundle.getBundle("dictionary").getString("ERROR_MESSAGE"), messaging);
	}

	public void helloMessage(String userFirstName, Messaging messaging) {
		sendSimpleMessage(String.format(ResourceBundle.getBundle("dictionary")
				.getString("HELLO_MESSAGE"), userFirstName), messaging);
	}

	public Button getPButton(String title, String payload) {
		Button button = new Button();
		button.setTitle(title);
		button.setType("postback");
		button.setPayload(payload);
		return button;
	}

	public void sendPostbackButtons(Messaging messaging, String text, Button... buttons) {
		sendButtons(Arrays.asList(buttons), text, messaging, "button", "template");
	}

	public void sendSimpleQuestion(String payload, Messaging messaging, String text) {
		sendQuickReplies(text, messaging, new QuickReply("Yes", payload + "&1"),
				new QuickReply("No", payload + "&0"));
	}

	public void sendFillingsAsQuickReplies(String text, Messaging messaging,
										   Page<ProductFilling> fillingList, int firstEl) {

		List<QuickReply> list = fillingList.stream()
				.map(f -> new QuickReply(f.getName(), PayloadUtils.createPayloadWithParams(COMMON_Q_PAYLOAD.name(),
						String.valueOf(f.getId()), String.valueOf(firstEl)))).collect(toList());

		additionalButtons(list, firstEl, fillingList.getTotalPages() - 1, AdditionalButtonCases.FILLINGS);
		sendQuickReplies(text, messaging, list.toArray(new QuickReply[0]));
	}

	public void sendTypedQuickReply(String text, Messaging messaging, String type) {
		QuickReply quickReply = new QuickReply();
		quickReply.setContentType(type);

		sendQuickReplies(text, messaging, quickReply);
	}

	public void sendUsersAsQuickReplies(Messaging messaging, Page<MessengerUser> users) {

		List<QuickReply> list = users.stream()
				.map(u -> {
					QuickReply quickReply = new QuickReply(u.getFirstName() + " " + u.getLastName(),
							PayloadUtils.createPayloadWithParams(
									COMMON_Q_PAYLOAD.name(),
									String.valueOf(u.getId()), String.valueOf(users.getNumber())));
					quickReply.setImageUrl(u.getImage());
					return quickReply;
				}).collect(toList());

		additionalButtons(list, users.getNumber(), users.getTotalPages() - 1, AdditionalButtonCases.USERS);
		sendQuickReplies("Choose user: ", messaging, list.toArray(new QuickReply[0]));
	}

	private void additionalButtons(List<QuickReply> list, int pageNumber, int totalPages,
								   AdditionalButtonCases buttonCase) {

		if (buttonCase == AdditionalButtonCases.FILLINGS) {
			list.add(new QuickReply("That`s it", CurtainQuickReplyParser.QuickReplyPayload.STOP_Q_PAYLOAD.name()));
		}

		if (pageNumber > 0) {

			list.add(0, new QuickReply(" <- previous",
					PayloadUtils.createPayloadWithParams(CurtainQuickReplyParser.QuickReplyPayload
							.PREV_Q_PAYLOAD.name(), String.valueOf(pageNumber))));
		}

		if (pageNumber < totalPages) {
			list.add(new QuickReply("next -> ", PayloadUtils.createPayloadWithParams(CurtainQuickReplyParser.
					QuickReplyPayload.NEXT_Q_PAYLOAD.name(), String.valueOf(pageNumber))));
		}
	}
}
