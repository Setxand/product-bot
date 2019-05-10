package com.productbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MessengerUser {

	public enum UserStatus {

		CREATE_FILLING1,
		CREATE_PROD1,
		CREATE_PROD2,
		CREATE_PROD3,
		CREATE_PROD4,
		CREATE_PROD5,
		ORDERING1,
		ORDERING2,
		ORDERING3

	}

	@Id
	private Long id;
	private String firstName;
	private String lastName;
	private Locale locale;

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	@Enumerated(EnumType.STRING)
	private Role role;

}
