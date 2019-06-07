package com.productbot.service;

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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class ProductService {

	public enum MenuType {
		ADD,
		ORDER,
		UPDATE
	}

	private final ProductValidator productValidator;
	private final FillingRepository fillingRepo;
	private final ProductRepository productRepo;
	private final ProductHelperService productHelper;

	public ProductService(ProductValidator productValidator, FillingRepository fillingRepo,
						  ProductRepository productRepo) {
		this.productValidator = productValidator;
		this.fillingRepo = fillingRepo;
		this.productRepo = productRepo;
		this.productHelper = new ProductHelperService(productValidator, productRepo);
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
				throw new BotException(messaging, "Simply get control panel with the help of your context menu, " +
						"or enter: 'navigation' in your chat string.");
		}

	}

	public ProductFilling getFilling(String fillingId) {
		return fillingRepo.findById(fillingId).orElseThrow(() -> new IllegalArgumentException("Invalid filling ID"));
	}

	@Transactional
	public Product productCreated(Messaging messaging) {
		Product product = productRepo.findByMetaInfAndIsOwn(messaging.getSender().getId().toString(), false);
		product.setMetaInf(null);
		return product;
	}

	@Transactional
	public void addProdFilling(Messaging messaging) {
		Product product = productRepo.findByMetaInfAndIsOwn(messaging.getSender().getId().toString(), false);

		String[] params = PayloadUtils.getParams(messaging.getMessage().getQuickReply().getPayload());
		String fillingId = params[0];
		product.getFillings().add(fillingId);
	}

	public Element getElement(String productId) {
		Product product = getProduct(productId);
		String fillings = product.getFillings().stream().map(f -> getFilling(f).getName())
				.collect(Collectors.joining(", "));

		return DtoUtils.element(product, fillings);
	}

	public List<Element> getMenuElements(int page, MenuType menuType) {
		Page<Product> products = getProducts(PageRequest.of(page, 10));
		Map<String, String> fillingsMap = products.stream().collect(toMap(Product::getId,
				p -> p.getFillings().stream().map(f -> getFilling(f).getName()).collect(Collectors.joining(", "))));

		List<Element> list = products.stream().map(p -> DtoUtils
				.element(p, fillingsMap.get(p.getId()), productHelper.getButtons(menuType, p.getId())))
				.collect(Collectors.toList());

		if (products.hasNext())
			productHelper.directionButtons(list, products, menuType, true);
		if (products.hasPrevious())
			productHelper.directionButtons(list, products, menuType, false);

		return list;
	}

	public Product getProduct(String id) {
		return productRepo.findById(id).orElseGet(() -> {
			Product product = new Product();
			product.setName("Deleted product");
			return product;
		});
	}

	@Transactional
	public void deleteProduct(String productId) {
		productRepo.deleteById(productId);
	}
}
