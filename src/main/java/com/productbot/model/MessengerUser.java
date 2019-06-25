package com.productbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
		ORDERING3,
		SETTING_ROLE1,
		DEL_FILLING

	}

	@Id
	private Long id;
	private String firstName;
	private String lastName;
	private Locale locale;
	private String image;
	private String adminMetaInfo;
	private String platform;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Product> ownProducts = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	@Enumerated(EnumType.STRING)
	private Role role;

}
