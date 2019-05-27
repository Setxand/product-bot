package com.productbot.service;

import com.messanger.Messaging;
import com.productbot.model.Product;
import com.productbot.repository.ProductRepository;
import com.productbot.validator.ProductValidator;


public class ProductHelperService {

	private final ProductValidator productValidator;
	private final ProductRepository productRepo;

	public ProductHelperService(ProductValidator productValidator, ProductRepository productRepo) {
		this.productValidator = productValidator;
		this.productRepo = productRepo;
	}

	void setProductName(Messaging messaging) {
		Product product = new Product();
		product.setName(messaging.getMessage().getText());
		product.setMetaInf(messaging.getSender().getId().toString());
		productRepo.save(product);
	}

	void setProductImage(Messaging messaging) {
		String text = messaging.getMessage().getText();
		productValidator.validateUrl(text, messaging);
		Product product = productRepo.findByMetaInfAndIsOwn(messaging.getSender().getId().toString(), true);
		product.setImage(messaging.getMessage().getText());
	}

	void setProductPrice(Messaging messaging) {
		Product product = productRepo.findByMetaInfAndIsOwn(messaging.getSender().getId().toString(), true);
		float price = productValidator.validatePriceAndReturnVal(messaging.getMessage().getText(), messaging);
		product.setPrice(price);
	}
}
