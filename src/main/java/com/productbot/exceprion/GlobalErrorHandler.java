package com.productbot.exceprion;

import com.productbot.client.MessengerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalErrorHandler {

	@Autowired MessengerClient messengerClient;

	@ExceptionHandler(BotException.class)
	public void handleBotException(BotException ex) {
		messengerClient.errorMessage(ex.getId());
	}

}
