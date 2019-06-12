package com.productbot.dto;

import java.util.List;
import java.util.Set;

public class ProductDTO {

	public String id;
	public String name;
	public String image;
	public float price;
	public List<FillingDTO> fillings;
	public List<String> fillingsRequestData;

	public Set<String> keys;

}
