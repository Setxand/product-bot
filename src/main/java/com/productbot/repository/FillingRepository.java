package com.productbot.repository;

import com.productbot.model.ProductFilling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FillingRepository extends JpaRepository<ProductFilling, String> {
	List<ProductFilling> findAllByIdIn(List<String> ids);
}
