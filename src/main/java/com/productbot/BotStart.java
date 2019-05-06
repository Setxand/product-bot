package com.productbot;

import com.messanger.*;
import com.productbot.client.MessengerClient;
import com.productbot.client.Platform;
import com.productbot.client.UrlProps;
import com.productbot.service.Payload;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

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

		/// CURTAIN
		curtainRequests(shell);

		/// COMMON

		MessengerProfileApi messengerProfileApi = new MessengerProfileApi();
		shell.setPlatform(Platform.COMMON);
		messengerProfileApi.setPlatform(Platform.COMMON);

		PersistentMenu persistentMenu = new PersistentMenu();
		persistentMenu.setCallToActions(Arrays.asList(new MenuItem("postback", "Menu of croissants", "MENU_PAYLOAD")
				, new MenuItem("postback", "Navigation menu", "NAVIGATION_MENU")));
		messengerProfileApi.setPersistentMenu(Collections.singletonList(persistentMenu));

		messengerClient.sendRequest(messengerProfileApi, "messenger_profile");
		messengerClient.sendRequest(shell, "messenger_profile");
	}

	private void curtainRequests(Shell shell) {
		shell.setPlatform(Platform.CURTAIN);

		MessengerProfileApi messengerProfileApi = new MessengerProfileApi();
		messengerProfileApi.setPlatform(Platform.CURTAIN);
		messengerProfileApi.setGetStarted(new GetStarted("GET_STARTED_PAYLOAD"));
		PersistentMenu persistentMenu = new PersistentMenu();
		persistentMenu.setCallToActions(Collections.singletonList(new MenuItem("postback", "Navigation",
										Payload.NAVI_PAYLOAD.name())));

		messengerProfileApi.setPersistentMenu(Collections.singletonList(persistentMenu));


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
