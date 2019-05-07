package com.productbot.service;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.Product;
import com.productbot.model.ProductFilling;
import com.productbot.repository.FillingRepository;
import com.productbot.repository.ProductRepository;
import com.productbot.utils.QuickReplyUtils;
import com.productbot.validator.ProductValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {

	private final ProductValidator productValidator;
	private final FillingRepository fillingRepo;
	private final ProductRepository productRepo;

	public ProductService(ProductValidator productValidator, FillingRepository fillingRepo, ProductRepository productRepo) {
		this.productValidator = productValidator;
		this.fillingRepo = fillingRepo;
		this.productRepo = productRepo;
	}

	public String getProductFillingsAsString() {
		List<ProductFilling> all = fillingRepo.findAll();
		StringBuilder stringBuilder = new StringBuilder();
		all.forEach(e -> stringBuilder.append("\n").append(e.getName()).append(" - ").append(e.getPrice()));
		return stringBuilder.toString();
	}

	public Page<ProductFilling> getProductFillings(Pageable pageable) {
		return fillingRepo.findAll(pageable);
	}

	@Transactional
	public void createFilling(Messaging messaging) {
		String text = messaging.getMessage().getText();
		if (!productValidator.validateFillingInputString(text)) throw new BotException(messaging);

		String[] fillingText = text.split(",");
		ProductFilling filling = new ProductFilling();
		filling.setName(fillingText[0]);
		filling.setPrice(Float.valueOf(fillingText[1]));
		fillingRepo.saveAndFlush(filling);
	}

	@Transactional
	public void createProduct(Messaging messaging, MessengerUser.UserStatus status) {
		switch (status) {

			case CREATE_PROD1:
				createProd1(messaging);
				break;

			case CREATE_PROD2:
				createProd2(messaging);
				break;

			default:
				throw new BotException(messaging);
		}

	}

	@Transactional
	public void addProdFilling(Messaging messaging) {
		Product product = productRepo.findByMetaInf(messaging.getSender().getId().toString());

		String[] params = QuickReplyUtils.getParams(messaging.getMessage().getQuickReply().getPayload());
		String fillingId = params[0];
		product.getFillings().add(fillingId);
	}

	private void createProd2(Messaging messaging) {
		String text = messaging.getMessage().getText();
		productValidator.validateUrl(text, messaging);
		Product product = productRepo.findByMetaInf(messaging.getSender().getId().toString());
		product.setImage(messaging.getMessage().getText());
	}

	private void createProd1(Messaging messaging) {
		Product product = new Product();
		product.setName(messaging.getMessage().getText());
		product.setMetaInf(messaging.getSender().getId().toString());
		productRepo.save(product);
	}
}
