package com.productbot.utils;

import com.messanger.Button;
import com.messanger.Element;
import com.messanger.UserData;
import com.productbot.model.MessengerUser;
import com.productbot.model.Product;

import java.util.Arrays;

public class DtoUtils {

	public static UserData user(MessengerUser user) {
		UserData userData = new UserData();
		userData.setFirstName(user.getFirstName());
		userData.setLastName(user.getLastName());
		userData.setLocale(user.getLocale());
		return userData;
	}

	public static Element element(Product product, String fillings, Button ...button) {
		Element element = new Element();
		element.setTitle(product.getName() + " - " + product.getPrice());
		element.setImage_url(product.getImage());
		element.setSubtitle(fillings);
		element.setButtons(Arrays.asList(button));
		return element;
	}
}
