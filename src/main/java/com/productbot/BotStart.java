package com.productbot;

import com.messanger.*;
import com.productbot.client.MessengerClient;
import com.productbot.client.Platform;
import com.productbot.client.UrlProps;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class BotStart {

	private final MessengerClient messengerClient;
	private final UrlProps urlProps;

	public BotStart(MessengerClient messengerClient, UrlProps urlProps) {
		this.messengerClient = messengerClient;
		this.urlProps = urlProps;
	}

	@PostConstruct
	public void startBot() {
		String SERVER_URL = urlProps.getMap().get("server");
		Shell shell = new Shell();
		shell.setWhiteListedDomains(Arrays.asList(SERVER_URL));
		shell.setPlatform(Platform.COMMON);

		MessengerProfileApi messengerProfileApi = new MessengerProfileApi(new GetStarted("GET_STARTED_PAYLOAD"),
				new ArrayList<PersistentMenu>());
		messengerProfileApi.setPlatform(Platform.COMMON);
		PersistentMenu persistentMenu = new PersistentMenu();
		persistentMenu.setCallToActions(Arrays.asList(new MenuItem("postback", "Menu of croissants", "MENU_PAYLOAD")
				, new MenuItem("postback", "Navigation menu", "NAVIGATION_MENU")));
		messengerProfileApi.getPersistentMenu().add(persistentMenu);

		messengerClient.sendRequest(messengerProfileApi, "messenger_profile");
		messengerClient.sendRequest(shell, "messenger_profile");
	}

	@Component
	class ContextRefresh implements ApplicationListener<ApplicationReadyEvent> {

		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {
			messengerClient.setWebHooks();
		}
	}

}
