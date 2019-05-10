package com.productbot.repository;

import com.productbot.model.ProductBucket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBucketRepository extends JpaRepository<ProductBucket, String> {
}
