package com.productbot.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Courier {

	@Id
	private String id;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ProductBucket> bucketList = new ArrayList<>();

}
