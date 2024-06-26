package com.banking.jbs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.jbs.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
	
	Boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	
	Boolean existsByAccountNumber(String accountNumber);
	
	User findByAccountNumber(String accountNumber);

}
