package com.productbot.repository;

import com.productbot.model.ProductFilling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FillingRepository extends JpaRepository<ProductFilling, String> {
}
