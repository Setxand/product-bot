package com.productbot.repository;

import com.productbot.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, String> {
}
