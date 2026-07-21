package com.crm.repos;

import com.crm.entites.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

	Optional<User> findByPhone(String phone);
	
}
