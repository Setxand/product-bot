package com.productbot.controller;

import com.messanger.Event;
import com.productbot.client.Platform;
import com.productbot.service.DirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class WebhookController {

	@Autowired DirectionService directionService;

	@GetMapping({"/v1/common-webhook", "/v1/service-webhook"})
	public String commonVerify(@RequestParam(name = "hub.verify_token") String verifyToken,
							   @RequestParam(name = "hub.challenge") String challenge) {

		if (!verifyToken.equals("VerTok")) throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		return challenge;
	}

	@PostMapping("/v1/common-webhook")
	public void commonListenEntry(@RequestBody Event event) {
		directionService.directEvent(event, Platform.COMMON);
	}

	@PostMapping("/v1/service-webhook")
	public void serviceListenEntry(@RequestBody Event event) {
		directionService.directEvent(event, Platform.CURTAIN);
	}
}
