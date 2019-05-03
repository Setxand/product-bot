package com.productbot.exceprion;

import org.springframework.http.HttpStatus;

public class BotException extends RuntimeException {

	private Long id;

	public BotException(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
