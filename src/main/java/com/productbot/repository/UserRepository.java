package com.productbot.repository;

import com.productbot.model.MessengerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<MessengerUser, Long> {

	List<MessengerUser> findAllByFirstNameAndLastName(String firstName, String lastName);

}
