package com.productbot.validator;

import com.messanger.Messaging;
import com.productbot.dto.ProductDTO;
import com.productbot.exceprion.BotException;
import com.productbot.model.Product;
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
			if (text.length() > 255) throw new IllegalArgumentException();
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

	public void validateProduct(ProductDTO dto, Product product) {

		if (dto.keys.contains("name") && dto.name.equals(product.getName()) && !dto.name.equals("")) {
			dto.keys.remove("name");
		}

		if (dto.keys.contains("price") && dto.price == product.getPrice()) {
			dto.keys.remove("price");
		}
		if (dto.keys.contains("image") && dto.image.equals(product.getImage()) && !dto.image.equals("")) {
			dto.keys.remove("image");
		}

	}
}
