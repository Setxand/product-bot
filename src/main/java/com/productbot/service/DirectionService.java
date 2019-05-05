package com.productbot.service;

import com.messanger.Event;
import com.messanger.Messaging;
import com.productbot.client.Platform;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectionService {

	private final PostbackParserService postbackParser;

	public DirectionService(PostbackParserService postbackParser) {
		this.postbackParser = postbackParser;
	}

	public void directEvent(Event event, Platform platform) {
		if (event.getObject().equals("page")) {

			event.getEntry().forEach(entry -> {
				if (entry.getMessaging() != null) {
					directMessaging(entry.getMessaging(), platform);
				}
			});
		}
	}

	private void directMessaging(List<Messaging> messaging, Platform platform) {
		messaging.forEach(ms -> {
			ms.setPlatform(platform);

			if (ms.getMessage() != null)
				passMessage(ms);

			if (ms.getPostback() != null)
				passPostBack(ms);
		});
	}

	private void passPostBack(Messaging messaging) {
		switch (messaging.getPostback().getPayload()) {

			case "GET_STARTED_PAYLOAD":
				postbackParser.getStarted(messaging);
				break;

			default:
				throw new RuntimeException("Internal server error");
		}
	}

	private void passMessage(Messaging messaging) {
		postbackParser.getStarted(messaging);
	}
}
