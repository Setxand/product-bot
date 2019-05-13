package com.productbot.service;

import com.messanger.Messaging;
import com.productbot.exceprion.BotException;
import com.productbot.model.MessengerUser;
import com.productbot.model.ProductBucket;
import com.productbot.repository.ProductBucketRepository;
import com.productbot.utils.PayloadUtils;
import com.productbot.validator.ProductValidator;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ProductBucketService {

	private final ProductBucketRepository bucketRepo;
	private final ProductValidator productValidator;

	public ProductBucketService(ProductBucketRepository bucketRepo, ProductValidator productValidator) {
		this.bucketRepo = bucketRepo;
		this.productValidator = productValidator;
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
		getBucket(messaging).setOrderProcess(null);
	}

	@Transactional
	public void addProd(Messaging messaging) {
		ProductBucket bucket = getBucket(messaging);
		String payload = messaging.getPostback().getPayload();

		String productToAdd = PayloadUtils.getParams(payload)[0];
		bucket.getProducts().add(productToAdd);
	}

	private void ordering2(Messaging messaging) {
		ProductBucket bucket = getBucket(messaging);
		bucket.setLocation(messaging.getMessage().getText());
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
