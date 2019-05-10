package com.productbot.service;

import com.messanger.Event;
import com.messanger.Messaging;
import com.productbot.client.Platform;
import com.productbot.processor.Processor;
import com.productbot.processor.impl.CommonProcessor;
import com.productbot.processor.impl.CurtainProcessor;
import com.productbot.service.common.CommonMessageParser;
import com.productbot.service.common.CommonPostbackParser;
import com.productbot.service.common.CommonQuickReplyParser;
import com.productbot.service.curtain.CurtainMessageParser;
import com.productbot.service.curtain.CurtainPostbackParser;
import com.productbot.service.curtain.CurtainQuickReplyParser;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DirectionService {

	private static final Logger log = Logger.getLogger(DirectionService.class);
	private final Map<Platform, Processor> processors;

	public DirectionService(CommonPostbackParser commonPostbackParser, CurtainPostbackParser curtainPostbackParser,
							CurtainMessageParser curtainMessageParser, CurtainQuickReplyParser curtainQuickReplyParser,
							CommonMessageParser commonMessageParser, CommonQuickReplyParser commonQuickReplyParser) {

		this.processors = new HashMap<>();
		this.processors.put(Platform.COMMON, new CommonProcessor(commonPostbackParser, commonMessageParser,
				commonQuickReplyParser));
		this.processors.put(Platform.CURTAIN, new CurtainProcessor(curtainPostbackParser, curtainMessageParser,
				curtainQuickReplyParser));
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
			if (ms.getMessage() != null) {

				if (ms.getMessage().getQuickReply() != null) {
					processors.get(platform).passQuickReply(ms);

				} else
					processors.get(platform).passMessage(ms);
			}

			if (ms.getPostback() != null)
				processors.get(platform).passPostback(ms);
		});
	}
}
