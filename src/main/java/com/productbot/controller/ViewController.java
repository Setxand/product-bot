package com.productbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

	@GetMapping("/v1/products")
	public String getProductUpdating() {
		return "updateProduct";
	}

}
