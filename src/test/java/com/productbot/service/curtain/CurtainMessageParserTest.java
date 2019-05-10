//package com.productbot.service.curtain;
//
//import com.messanger.Element;
//import com.messanger.Messaging;
//import com.productbot.client.curtain.CurtainMessengerClient;
//import com.productbot.model.Product;
//import com.productbot.model.ProductFilling;
//import com.productbot.service.ProductService;
//import com.productbot.service.UserService;
//import com.productbot.utils.DtoUtils;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class CurtainMessageParserTest {
//
//	@Mock CurtainMessengerClient curtainMessengerClient;
//	@Mock UserService userService;
//	@Mock ProductService productService;
//
//	@Captor ArgumentCaptor<ArrayList<Element>> captor;
//
//	@InjectMocks CurtainMessageParser curtainMessageParser;
//
//	@Before
//	public void setUp() {
//	}
//
//	@Test
//	public void sendGeneric() {
//		Page<Product> products = createProduct(10);
//		Messaging messaging = new Messaging();
//		when(productService.getProducts(PageRequest.of(0, 10))).thenReturn(products);
//
//		ProductFilling filling1 = createFilling("1");
//		ProductFilling filling2 = createFilling("2");
//		when(productService.getFilling("1", messaging)).thenReturn(filling1);
//		when(productService.getFilling("2", messaging)).thenReturn(filling2);
//
//		curtainMessageParser.sendGeneric(messaging);
//
//		verify(curtainMessengerClient).sendGenericTemplate(captor.capture(), eq(messaging));
//
//		List<Element> collect = products.stream().map(p -> DtoUtils.element(p, null, button(p.getId())))
//				.peek(e -> e.setSubtitle(filling1.getName() + ", " + filling2.getName() + " "))
//				.collect(Collectors.toList());
//
//		Assert.assertEquals(collect.get(0).getTitle(), captor.getValue().get(0).getTitle());
//		Assert.assertEquals(collect.get(0).getSubtitle(), captor.getValue().get(0).getSubtitle());
//
//		Assert.assertEquals(collect.get(9).getTitle(), captor.getValue().get(9).getTitle());
//		Assert.assertEquals(collect.get(9).getSubtitle(), captor.getValue().get(9).getSubtitle());
//	}
//
//	private static String randomString() {
//		return UUID.randomUUID().toString();
//	}
//
//	public static ProductFilling createFilling(String id) {
//		ProductFilling filling = new ProductFilling();
//		filling.setName(randomString());
//		filling.setPrice(3.0f);
//		filling.setId(id);
//		return filling;
//	}
//
//	public static Page<Product> createProduct(int count) {
//		List<Product> list = new ArrayList<>();
//		for (int i = 0; i < count; i++) {
//			Product product = new Product();
//			product.setName(randomString());
//			product.setImage(randomString());
//			product.setFillings(Arrays.asList("1", "2"));
//			product.setPrice(4.0f);
//			product.setId(randomString());
//			list.add(product);
//		}
//		return new PageImpl<>(list);
//	}
//}