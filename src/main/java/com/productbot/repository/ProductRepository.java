package com.productbot.repository;

import com.productbot.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

	Product findByMetaInfAndIsOwn(String metaInf, Boolean isOwn);

}
