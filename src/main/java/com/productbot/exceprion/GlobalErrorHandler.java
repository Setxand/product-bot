package com.productbot.exceprion;

import com.productbot.client.MessengerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler {

	@Autowired MessengerClient messengerClient;

	@ExceptionHandler(BotException.class)
	public void handleBotException(BotException ex) {
		if (ex.getMessage() != null) {

			messengerClient.sendSimpleMessage(ex.getMessage(), ex.getMessaging());
		} else {

			messengerClient.errorMessage(ex.getMessaging());
		}
	}

//	@ExceptionHandler(Exception.class)
//	public void handleCommonException(Exception ex) {
//		messengerClient.sendSimpleMessage(ex.getLocalizedMessage());
//	}

}
