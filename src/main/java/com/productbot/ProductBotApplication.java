package com.productbot;

import com.productbot.client.UrlProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(UrlProps.class)
public class ProductBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductBotApplication.class, args);
	}

}
