package com.productbot.utils;

import com.messanger.Button;
import com.messanger.Element;
import com.messanger.UserData;
import com.productbot.dto.FillingDTO;
import com.productbot.dto.ProductDTO;
import com.productbot.model.MessengerUser;
import com.productbot.model.Product;
import com.productbot.model.ProductBucket;
import com.productbot.model.ProductFilling;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DtoUtils {

	public static FillingDTO filling(ProductFilling entity) {
		FillingDTO dto = new FillingDTO();
		dto.name = entity.getName();
		dto.price = entity.getPrice();
		dto.id = entity.getId();
		return dto;
	}

	public static Element orderingElement(ProductBucket productBucket, Map<String, String> userNames,
										  Map<String, List<String>> productMap, Button... buttons) {
		Element element = new Element();
		element.setTitle(userNames.get(productBucket.getUserId()) + ", " + productBucket.getPhone());
		element.setSubtitle(String.join(",", productMap.get(productBucket.getId())));
		element.setImage_url("http://cdn.onlinewebfonts.com/svg/download_568523.png");

		element.setButtons(Arrays.asList(buttons));
		return element;
	}

	public static UserData user(MessengerUser user) {
		UserData userData = new UserData();
		userData.setFirstName(user.getFirstName());
		userData.setLastName(user.getLastName());
		userData.setLocale(user.getLocale());
		return userData;
	}

	public static Element element(Product product, String fillings, Button... button) {
		Element element = new Element();
		element.setTitle(product.getName() + " - " + product.getPrice());
		element.setImage_url(product.getImage());
		element.setSubtitle(fillings);
		element.setButtons(Arrays.asList(button));
		return element;
	}

	public static ProductDTO product(Product entity) {
		ProductDTO dto = new ProductDTO();
		dto.id = entity.getId();
		dto.name = entity.getName();
		dto.image = entity.getImage();
		dto.price = entity.getPrice();
		return dto;
	}
}
