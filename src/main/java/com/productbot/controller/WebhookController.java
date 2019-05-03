package com.productbot.controller;

import com.messanger.Event;
import com.productbot.service.DirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class WebhookController {

	@Autowired DirectionService directionService;

	@GetMapping("/v1/webhook")
	public String verify(@RequestParam(name = "hub.verify_token") String verifyToken,
						 @RequestParam(name = "hub.challenge") String challenge) {

		if (!verifyToken.equals("VerTok")) throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		return challenge;
	}

	@PostMapping("/v1/webhook")
	public void listenEntry(@RequestBody Event event) {
		directionService.directEvent(event);
	}
}