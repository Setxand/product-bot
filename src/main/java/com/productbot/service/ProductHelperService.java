package com.productbot.service;

import com.messanger.Button;
import com.messanger.Element;
import com.messanger.Messaging;
import com.productbot.client.UrlProps;
import com.productbot.model.Product;
import com.productbot.repository.ProductRepository;
import com.productbot.utils.PayloadUtils;
import com.productbot.validator.ProductValidator;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.productbot.service.PostbackPayload.*;


public class ProductHelperService {

	private final ProductValidator productValidator;
	private final ProductRepository productRepo;
	private final UrlProps urlProps;

	public ProductHelperService(ProductValidator productValidator, ProductRepository productRepo, UrlProps urlProps) {
		this.productValidator = productValidator;
		this.productRepo = productRepo;
		this.urlProps = urlProps;
	}

	void setProductName(Messaging messaging) {
		Product product = new Product();
		product.setName(messaging.getMessage().getText());
		product.setMetaInf(messaging.getSender().getId().toString());
		productRepo.save(product);
	}

	@Transactional
	void setProductImage(Messaging messaging) {
		String text = messaging.getMessage().getText();
		productValidator.validateUrl(text, messaging);
		Product product = productRepo.findByMetaInfAndIsOwn(messaging.getSender().getId().toString(), false);
		product.setImage(text);
	}

	void setProductPrice(Messaging messaging) {
		Product product = productRepo.findByMetaInfAndIsOwn(messaging.getSender().getId().toString(), false);
		float price = productValidator.validatePriceAndReturnVal(messaging.getMessage().getText(), messaging);
		product.setPrice(price);
	}

	public Button[] getButtons(ProductService.MenuType menuType, String productId) {

		switch (menuType) {
			case ADD:
				return new Button[]{new Button("Add",
						PayloadUtils.createPayloadWithParams(ADD_PRODUCT_PAYLOAD.name(), productId))};

			case ORDER:
				return new Button[]{new Button("Order",
						PayloadUtils.createPayloadWithParams(ORDER_PAYLOAD.name(), productId))};

			case UPDATE:
				return new Button[]{
						new Button("Update")
								.urlButton(urlProps.getMap().get("server") + "/v1/products/" + productId).webView(),
						new Button("Delete",
								PayloadUtils.createPayloadWithParams(DELETE_PRODUCT_PAYLOAD.name(), productId))};

			default:
				throw new RuntimeException();
		}
	}

	public void directionButtons(List<Element> list, Page<Product> products, ProductService.MenuType menuType,
								 boolean isNext) {

		ArrayList<Button> buttons = new ArrayList<>(list.get(isNext ? 9 : 0).getButtons());

		buttons.add(new Button(isNext ? "Next ->" : "<- Previous",
				PayloadUtils.createPayloadWithParams(isNext ? NEXT_PROD_PAYLOAD.name() : PREV_PROD_PAYLOAD.name(),
						"" + products.getNumber(), menuType.name())));

		list.get(isNext ? 9 : 0).setButtons(buttons);
	}
}
