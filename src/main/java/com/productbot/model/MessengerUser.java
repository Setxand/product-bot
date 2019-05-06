package com.productbot.model;

import com.productbot.service.UserStatus;
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
