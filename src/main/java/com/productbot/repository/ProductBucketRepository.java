package com.productbot.repository;

import com.productbot.model.ProductBucket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductBucketRepository extends JpaRepository<ProductBucket, String> {

	Optional<ProductBucket> findByUserIdAndOrderProcessIsTrue(String userId);

}
