package com.productbot.service;

import com.messanger.Message;
import com.messanger.Messaging;
import com.messanger.Recipient;
import com.productbot.repository.FillingRepository;
import com.productbot.repository.ProductRepository;
import com.productbot.validator.ProductValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

	@Mock ProductValidator productValidator;
	@Mock FillingRepository fillingRepo;
	@Mock ProductRepository productRepo;
	@Mock ProductHelperService productHelper;

	@InjectMocks ProductService productService;

	@Test
	public void menuOfEls() {

		Messaging messaging = new Messaging(new Message(), new Recipient(2323L));

	}


}