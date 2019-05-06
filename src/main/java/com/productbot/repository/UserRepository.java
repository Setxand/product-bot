package com.productbot.repository;

import com.productbot.model.MessengerUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<MessengerUser, Long> {
}
