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
public class ProductBucket {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;
	private String userId;
	private String phone;

	@Column(length = 510)
	private String location;

	// There is only one process per user might be true
	private Boolean orderProcess;

	@ElementCollection
	private List<String> products = new ArrayList<>();
}
