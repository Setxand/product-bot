package com.productbot.service;

import com.messanger.Event;
import com.messanger.Messaging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectionService {

	private final PostbackParserService postbackParser;

	public DirectionService(PostbackParserService postbackParser) {
		this.postbackParser = postbackParser;
	}

	public void directEvent(Event event) {
		if (event.getObject().equals("page")) {

			event.getEntry().forEach(entry -> {
				if (entry.getMessaging() != null) {

					directMessaging(entry.getMessaging());
				}
			});
		}
	}

	private void directMessaging(List<Messaging> messaging) {
		messaging.forEach(ms -> {

			if (ms.getMessage() != null)
				passMessage(ms);

			if (ms.getPostback() != null)
				passPostBack(ms);
		});
	}

	private void passPostBack(Messaging messaging) {
		switch (messaging.getPostback().getPayload()) {

			case "GET_STARTED_PAYLOAD":
				postbackParser.getStarted(messaging.getSender().getId());
				break;

			default:
				throw new RuntimeException("Internal server error");
		}
	}

	private void passMessage(Messaging messaging) {
		postbackParser.getStarted(messaging.getSender().getId());
	}
}
