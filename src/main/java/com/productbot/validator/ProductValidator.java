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
}
