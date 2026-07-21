package com.crm.controllers;

import com.crm.repos.UserRepo;
import com.crm.entites.User;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserApiController {
	private final UserRepo userRepo;
	
	
	public UserApiController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


	@GetMapping("/get_user_by_id")
	public User getById(@RequestParam Long id) {
		Optional<User> user = userRepo.findById(id);
			
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");		
		}
	}
	
	@PostMapping("/create_user")
	public Map<String, Long> createUser (@Valid @RequestBody User user) {
		User savedUser = userRepo.save(user);	
		return Map.of("user_id", savedUser.getId());
	}
	
	@GetMapping("/get_all_users")
	public Page<User> getAllUsers(@RequestParam int page, @RequestParam(defaultValue="10") int pageSize) {
	
		return userRepo.findAll(PageRequest.of(page, pageSize));		
		
	}

}
