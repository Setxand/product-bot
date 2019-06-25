package com.productbot.controller;

import com.productbot.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ManagementController {

	@Autowired ManagementService managementService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/v1/charges")
	public void createCharge(@RequestParam String stripeToken, @RequestParam Integer price,
							 @RequestParam String userId) {
		managementService.createCharge(stripeToken, price, userId);
	}

}
