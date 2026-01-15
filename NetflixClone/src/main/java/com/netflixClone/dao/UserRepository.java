package com.netflixClone.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netflixClone.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
	
	Optional<User> findByVerificationToken(String token);

	Optional<User> findByPasswordResetToken(String token);
	
	

}
