package com.productbot.service;

import com.messanger.Attachment;
import com.messanger.Element;
import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.Product;
import com.productbot.model.ProductBucket;
import com.productbot.repository.ProductBucketRepository;
import com.productbot.utils.DtoUtils;
import com.productbot.utils.PayloadUtils;
import com.productbot.validator.ProductValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductBucketService {

	private final ProductBucketRepository bucketRepo;
	private final ProductValidator productValidator;
	private final CourierService courierService;
	private final UserService userService;
	private final ProductService productService;

	public ProductBucketService(ProductBucketRepository bucketRepo, ProductValidator productValidator,
								CourierService courierService, UserService userService, ProductService productService) {
		this.bucketRepo = bucketRepo;
		this.productValidator = productValidator;
		this.courierService = courierService;
		this.userService = userService;
		this.productService = productService;
	}

	@Transactional
	public void makeOrder(Messaging messaging, MessengerUser.UserStatus status) {

		switch (status) {

			case ORDERING1:
				ordering1(messaging);
				break;

			case ORDERING2:
				ordering2(messaging);
				break;

			default:
				throw new BotException(messaging);
		}

	}

	@Transactional
	public void closeBucket(Messaging messaging) {
		ProductBucket bucket = getBucket(messaging);
		bucket.setOrderProcess(null);

		courierService.publishBucket(messaging, bucket);
	}

	@Transactional
	public void addProd(Messaging messaging) {
		ProductBucket bucket = getBucket(messaging);
		String payload = messaging.getPostback().getPayload();

		String productToAdd = PayloadUtils.getParams(payload)[0];
		bucket.getProducts().add(productToAdd);
	}

	public List<Element> getOrderingList(int pageNumber) {
		Page<ProductBucket> bucketPage = bucketRepo.findAllByAcceptedIsFalse(PageRequest.of(pageNumber, 10));
		List<ProductBucket> buckets = bucketPage.getContent();

		Map<String, String> userNames = userService.listUsersByIds(buckets.stream().map(ProductBucket::getUserId)
				.map(Long::parseLong)
				.collect(Collectors.toList())).stream()
				.collect(Collectors.toMap(u -> u.getId().toString(), u -> u.getFirstName() + " " + u.getLastName()));

		Map<String, List<String>> productMap = buckets.stream()
				.collect(Collectors.toMap(ProductBucket::getId, b -> b.getProducts().stream()
				.map(productService::getProduct).map(Product::getName).collect(Collectors.toList())));

		return buckets.stream()
				.map(productBucket -> DtoUtils.orderingElement(productBucket, userNames, productMap))
				.collect(Collectors.toList());
	}

	private void ordering2(Messaging messaging) {
		ProductBucket bucket = getBucket(messaging);
		Attachment attachment = messaging.getMessage().getAttachments().get(0);

		if (attachment.getType().equals("location")) {
			bucket.setLocation(attachment.getUrl());
		} else throw new BotException(messaging);
	}

	private void ordering1(Messaging messaging) {
		if (!bucketRepo.findByUserIdAndOrderProcessIsTrue(messaging.getSender().getId()
				.toString()).isPresent()) {

			ProductBucket bucket = new ProductBucket();
			bucket.setUserId(messaging.getSender().getId().toString());
			String productId = PayloadUtils.getParams(messaging.getPostback().getPayload())[0];
			bucket.getProducts().add(productId);
			bucket.setOrderProcess(true);
			bucketRepo.save(bucket);

		} else {
			ProductBucket bucket = getBucket(messaging);
			bucket.setPhone(productValidator.validatePhone(messaging));
		}
	}

	private ProductBucket getBucket(Messaging messaging) {
		return bucketRepo.findByUserIdAndOrderProcessIsTrue(messaging.getSender().getId()
				.toString()).orElseThrow(() -> new BotException(messaging));
	}
}
