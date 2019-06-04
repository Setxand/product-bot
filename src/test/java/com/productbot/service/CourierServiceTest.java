package com.productbot.service;

import com.messanger.Messaging;
import com.messanger.Sender;
import com.productbot.client.curtain.CurtainMessengerClient;
import com.productbot.model.MessengerUser;
import com.productbot.model.ProductBucket;
import com.productbot.model.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.productbot.client.Platform.COMMON;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourierServiceTest {

	@Mock UserService userService;
	@Mock CurtainMessengerClient messengerClient;
	@Mock ProductService productService;

	@InjectMocks CourierService courierService;

	Messaging messaging;
	ProductBucket bucket;

	@Before
	public void setUp() {
		messaging = createMessaging();
		bucket = createProductBucket();
	}

	@Test
	public void publishBucket() {

		Page<MessengerUser> users = createRandUsers(10);

		when(userService.findUsersByRole(Role.COURIER)).thenReturn(users);

//		courierService.publishBucket(messaging, bucket);
//
//		verify(messengerClient, times(10)).sendSimpleMessage(
//				eq(createOrderingString(bucket, any(MessengerUser.class))), any(Messaging.class));
//
//		assertEquals(bucket, users.get(0))
	}

	private String createOrderingString(ProductBucket bucket, MessengerUser user) {
		return "New Order!!!" +
				"\nname: " + user.getFirstName() + " " + user.getLastName() + "\nphone: " + bucket.getPhone() +
				"\nProducts: " + bucket.getProducts().stream().map(p -> productService.getProduct(p).getName())
				.collect(Collectors.joining(",")) +
				"\n\nlocation: \n" + bucket.getLocation();
	}

	private Page<MessengerUser> createRandUsers(int count) {
		ArrayList<MessengerUser> list = new ArrayList<>();
		MessengerUser messengerUser = new MessengerUser();
		for (int i = 0; i < count; i++) {
			messengerUser.setFirstName(UUID.randomUUID().toString());
			messengerUser.setLastName(UUID.randomUUID().toString());
			messengerUser.setImage(UUID.randomUUID().toString());
			messengerUser.setPlatform(UUID.randomUUID().toString());
			list.add(messengerUser);
		}
		return new PageImpl<>(list);
	}

	private ProductBucket createProductBucket() {
		ProductBucket bucket = new ProductBucket();
		bucket.setLocation("location-test");
		bucket.setPhone("phone-test");
		bucket.setUserId(messaging.getSender().getId().toString());
		bucket.setProducts(Arrays.asList("1", "2", "3"));
		return bucket;
	}

	private Messaging createMessaging() {
		Messaging messaging = new Messaging();
		messaging.setPlatform(COMMON);
		messaging.setSender(new Sender(234213L));
		return messaging;
	}
}