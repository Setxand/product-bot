package com.productbot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductBucket {

	@Id
	private String id;
	private String phone;
	private String location;

	@ElementCollection
	private List<String> products = new ArrayList<>();
}
