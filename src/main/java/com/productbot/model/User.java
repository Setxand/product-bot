package com.productbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Id
	private Long id;
	private String firstName;
	private String lastName;
	private Locale locale;

}
