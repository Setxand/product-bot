package com.productbot.repository;

import com.productbot.model.MessengerUser;
import com.productbot.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<MessengerUser, Long> {

	@Query(nativeQuery = true, value = "select * from " +
			"messenger_user where ((" +
			"first_name like %?1% and last_name like %?2%) " +
			"or first_name like %?1% " +
			"or last_name like %?1%) and platform = ?3")
	Page<MessengerUser> findAllByFirstNameAndLastName(String firstName, String lastName, String platform,
													  Pageable pageable);

	Page<MessengerUser> findUsersByRole(Role role, Pageable pageable);

	Optional<MessengerUser> findByIdAndPlatform(Long id, String platform);

	List<MessengerUser> findByIdIn(List<Long> id);
}
