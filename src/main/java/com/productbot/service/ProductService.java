package com.productbot.service;

import com.messanger.Button;
import com.messanger.Element;
import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.Product;
import com.productbot.model.ProductFilling;
import com.productbot.repository.FillingRepository;
import com.productbot.repository.ProductRepository;
import com.productbot.utils.DtoUtils;
import com.productbot.utils.PayloadUtils;
import com.productbot.validator.ProductValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.productbot.service.common.CommonPostbackParser.CommonPayload.*;
import static java.util.stream.Collectors.toMap;

@Service
public class ProductService {

	private final ProductValidator productValidator;
	private final FillingRepository fillingRepo;
	private final ProductRepository productRepo;
	private final ProductHelperService productHelper;

	public ProductService(ProductValidator productValidator, FillingRepository fillingRepo,
						  ProductRepository productRepo, ProductHelperService productHelper) {
		this.productValidator = productValidator;
		this.fillingRepo = fillingRepo;
		this.productRepo = productRepo;
		this.productHelper = productHelper;
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

	public Page<Product> getProducts(Pageable pageable) {
		return productRepo.findAll(pageable);
	}

	@Transactional
	public void createProduct(Messaging messaging, MessengerUser.UserStatus status) {
		switch (status) {

			case CREATE_PROD1:
				productHelper.setProductName(messaging);
				break;

			case CREATE_PROD2:
				productHelper.setProductImage(messaging);
				break;

			case CREATE_PROD4:
				productHelper.setProductPrice(messaging);
				break;

			default:
				throw new BotException(messaging, "The best practice is to use navigation)");
		}

	}

	public ProductFilling getFilling(String fillingId, Messaging messaging) {
		return fillingRepo.findById(fillingId).orElseThrow(() -> new BotException(messaging));
	}

	@Transactional
	public void productCreated(Messaging messaging) {
		Product product = productRepo.findByMetaInf(messaging.getSender().getId().toString());
		product.setMetaInf(null);
	}

	@Transactional
	public void addProdFilling(Messaging messaging) {
		Product product = productRepo.findByMetaInf(messaging.getSender().getId().toString());

		String[] params = PayloadUtils.getParams(messaging.getMessage().getQuickReply().getPayload());
		String fillingId = params[0];
		product.getFillings().add(fillingId);
	}

	public List<Element> getMenuElements(Messaging messaging, int page, Boolean addProduct) {
		Page<Product> products = getProducts(PageRequest.of(page, 10));
		Map<String, String> fillingsMap = products.stream().collect(toMap(Product::getId, p -> {

			StringBuilder stringBuilder = new StringBuilder();
			p.getFillings().forEach(f -> stringBuilder
					.append(getFilling(f, messaging).getName()).append(", "));

			stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
			return stringBuilder.toString();
		}));


		List<Element> list = products.stream().map(p -> DtoUtils
				.element(p, fillingsMap.get(p.getId()), getButton(addProduct, p.getId()))).collect(Collectors.toList());

		if (products.hasNext())
			directionButtons(list, products, addProduct, true);
		if (products.hasPrevious())
			directionButtons(list, products, addProduct, false);

		return list;
	}

	private void directionButtons(List<Element> list, Page<Product> products, Boolean addProduct, boolean isNext) {

		ArrayList<Button> buttons = new ArrayList<>(list.get(isNext ? 9 : 0).getButtons());

		buttons.add(new Button(isNext ? "Next ->" : "<- Previous",
				PayloadUtils.createPayloadWithParams(isNext ? NEXT_PROD_PAYLOAD.name() : PREV_PROD_PAYLOAD.name(),
						"" + products.getNumber(), Boolean.toString(addProduct))));

		list.get(isNext ? 9 : 0).setButtons(buttons);
	}

	private Button getButton(Boolean addProduct, String productId) {
		if (addProduct == null) return null;
		return addProduct ? new Button(
				"Add", PayloadUtils.createPayloadWithParams(ADD_PRODUCT_PAYLOAD.name(), productId)) :
				new Button("Order", PayloadUtils.createPayloadWithParams(ORDER_PAYLOAD.name(), productId));
	}
}
