package com.productbot.utils;

import com.messanger.Button;
import com.messanger.Element;
import com.messanger.UserData;
import com.messanger.utils.ButtonUtils;
import com.productbot.model.MessengerUser;
import com.productbot.model.Product;
import com.productbot.model.ProductBucket;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.productbot.service.PostbackPayload.GET_ORDER_PAYLOAD;

public class DtoUtils {

	public static Element orderingElement(ProductBucket productBucket, Map<String, String> userNames,
										  Map<String, List<String>> productMap, Button ...buttons) {
		Element element = new Element();
		element.setTitle(userNames.get(productBucket.getUserId()) + ", " + productBucket.getPhone());
		element.setSubtitle(String.join(",", productMap.get(productBucket.getId())));
		element.setImage_url("http://cdn.onlinewebfonts.com/svg/download_568523.png");

		element.setButtons(Arrays.asList(new Button("Get order", GET_ORDER_PAYLOAD.name()),
				ButtonUtils.getUrlButton("Location", productBucket.getLocation())));
		return element;
	}

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
