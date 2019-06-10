package com.productbot.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.productbot.dto.FillingDTO;
import com.productbot.dto.ProductDTO;
import com.productbot.model.Product;
import com.productbot.service.ProductService;
import com.productbot.utils.DtoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ViewController {

	@Autowired ProductService productService;
	@Autowired ObjectMapper objectMapper;

	@GetMapping("/v1/products/{productId}")
	public String getPr(@PathVariable String productId, Model model, Pageable pageable) {
		Product product = productService.getProduct(productId);

		ProductDTO dto = DtoUtils.product(product);
		dto.fillings = productService.getFillingsById(product.getFillings()).stream().map(DtoUtils::filling)
				.collect(Collectors.toList());

		Page<FillingDTO> fillingsPage = productService.getProductFillings(pageable).map(DtoUtils::filling);

		model.addAttribute("fillings", fillingsPage);
		model.addAttribute("product", dto);
		return "index";
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping("/v1/products")
	public void uopdateProduct(@RequestBody Map<String, Object> map) {
		ProductDTO dto = objectMapper.convertValue(map, ProductDTO.class);
		dto.keys = map.keySet();
		productService.updateProduct(dto);
	}

}
