package com.productbot.validator;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

	private final URLEditor urlEditor;

	public ProductValidator() {
		this.urlEditor = new URLEditor();
	}

	public boolean validateFillingInputString(String text) {

		String[] fillingComponents = text.split(",");
		if (fillingComponents.length != 2) {
			return false;
		}

		return true;
	}

	public void validateUrl(String text, Messaging messaging) {
		try {
			urlEditor.setAsText(text);
		} catch (IllegalArgumentException ex) {
			throw new BotException(messaging, "Invalid URL, try again");
		}

	}

	public float validatePriceAndReturnVal(String text, Messaging messaging) {
		try {
			return Float.valueOf(text);
		} catch (Exception e) {
			throw new BotException(messaging, "Invalid price, try again");
		}
	}

	public String validatePhone(Messaging messaging) {
		String phone = messaging.getMessage().getText();

		if (phone.length() != 13 || phone.chars().filter(ch -> ch == '+').count() != 1 ||!phone.startsWith("+")) {
			throw new BotException(messaging, "Incorrect phone");
		}

		return phone;
	}
}
