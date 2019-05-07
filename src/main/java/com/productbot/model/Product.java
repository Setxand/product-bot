package com.productbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;
	private String name;
	private String image;
	private Float price;

	// In general "metaInf" is used for creation product, and user id is set in. So it needs unique value.
	@Column(unique = true)
	private String metaInf;

	@ElementCollection
	private List<String> fillings = new ArrayList<>();

}
