package com.productbot.service;

import com.messanger.Messaging;
import com.messanger.Sender;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.exceprion.BotException;
import com.productbot.model.Courier;
import com.productbot.model.MessengerUser;
import com.productbot.model.ProductBucket;
import com.productbot.model.Role;
import com.productbot.repository.CourierRepository;
import com.productbot.repository.ProductBucketRepository;
import com.productbot.utils.PayloadUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

import static com.productbot.client.Platform.CURTAIN;
import static com.productbot.service.curtain.CurtainQuickReplyParser.QuickReplyPayload.PUBLISH_BUCKET;
import static com.productbot.service.curtain.CurtainQuickReplyParser.QuickReplyPayload.QUESTION_PAYLOAD;

@Service
public class CourierService {

	private final UserService userService;
	private final CurtainMessengerClient messengerClient;
	private final ProductService productService;
	private final CourierRepository courierRepo;
	private final ProductBucketRepository bucketRepo;

	public CourierService(UserService userService, CurtainMessengerClient messengerClient,
						  ProductService productService, CourierRepository courierRepo, ProductBucketRepository bucketRepo) {
		this.userService = userService;
		this.messengerClient = messengerClient;
		this.productService = productService;
		this.courierRepo = courierRepo;
		this.bucketRepo = bucketRepo;
	}

	public void publishBucket(Messaging messaging, ProductBucket bucket) {
		Page<MessengerUser> users = userService.findUsersByRole(Role.COURIER);

		MessengerUser user = userService.getUser(messaging.getSender().getId());
		String orderingString = createOrderingString(bucket, user);

		users.stream().filter(u -> u.getRole() == Role.COURIER).forEach(u -> {
			Messaging courierMessaging = new Messaging();

			courierMessaging.setPlatform(CURTAIN);
			courierMessaging.setSender(new Sender(u.getId()));

			messengerClient.sendSimpleMessage("New order!!!" + orderingString, courierMessaging);

			messengerClient.sendSimpleQuestion(PayloadUtils.createPayloadWithParams(QUESTION_PAYLOAD.name(),
					PUBLISH_BUCKET.name(),
					bucket.getId()),
					courierMessaging, "Accept this order?");//todo acceptance, send it to current user
		});
		messengerClient.sendSimpleMessage("Your order check: " + orderingString, messaging);
	}

	@Transactional
	public void addProductBucket(Messaging messaging, String bucketId) {
		ProductBucket productBucket = getBucket(bucketId);
		Courier courier = courierRepo
				.findById(messaging.getSender().getId().toString()).orElseThrow(() -> new BotException(messaging));

		courier.getBucketList().add(productBucket);
	}

	@Transactional
	public void createCourier(String id) {
		Courier courier = new Courier();
		courier.setId(id);
		courierRepo.save(courier);
	}

	public void courierAccepted(Messaging messaging) {
		Page<MessengerUser> users = userService.findUsersByRole(Role.COURIER);

		Messaging courierMessaging = new Messaging();
		users.stream().filter(u -> u.getRole() == Role.COURIER).forEach(u -> {
			MessengerUser user = userService.getUser(messaging.getSender().getId());

			courierMessaging.setSender(new Sender(u.getId()));
			courierMessaging.setPlatform(messaging.getPlatform());

			messengerClient.sendSimpleMessage("Accepted by a courier: " +
							user.getFirstName() + " " + user.getLastName(),
					courierMessaging);
		});
	}

	@Transactional
	public void acceptCourierOrder(Messaging messaging) {
		Courier courier = courierRepo.findById(messaging.getSender().getId().toString())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Courier ID"));

		String bucketId = PayloadUtils.getParams(messaging.getMessage().getQuickReply().getPayload())[1];
		ProductBucket bucket = getBucket(bucketId);
		bucket.setAccepted(true);
		courier.getBucketList().add(bucket);
	}

	private ProductBucket getBucket(String bucketId) {
		return bucketRepo.findById(bucketId).orElseThrow(() -> new IllegalArgumentException("Invalid Bucket ID"));
	}

	private String createOrderingString(ProductBucket bucket, MessengerUser user) {
		return "\nname: " + user.getFirstName() + " " + user.getLastName() + "\nphone: " + bucket.getPhone() +
				"\nProducts: " + bucket.getProducts().stream().map(p -> productService.getProduct(p).getName())
				.collect(Collectors.joining(",")) +
				"\n\nlocation: \n" + bucket.getLocation();
	}
}
