package com.crm.controllers;

import com.crm.repos.UserRepo;
import com.crm.entites.User;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

import java.util.Map;
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
		    if (user.getId() == null) {
		        // Find the maximum existing ID to auto-increment safely, or let JPA handle generation if configured with GenerationType.IDENTITY
		        Long maxId = userRepo.findAll().stream().mapToLong(User::getId).max().orElse(0L);
		        user.setId(maxId + 1);
		    }
		    User savedUser = userRepo.save(user);    
		    return Map.of("user_id", savedUser.getId());
		}
	
	@GetMapping("/get_all_users")
	public Page<User> getAllUsers(@RequestParam int page, @RequestParam(defaultValue="10") int pageSize) {
		return userRepo.findAll(PageRequest.of(page, pageSize));		
	}

	@PutMapping("/update_user/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUserDetails) {
		return userRepo.findById(id)
			.map(existingUser -> {
				existingUser.setName(updatedUserDetails.getName());
				existingUser.setPhone(updatedUserDetails.getPhone());
				existingUser.setPrefName(updatedUserDetails.getPrefName());
				User savedUser = userRepo.save(existingUser);
				return ResponseEntity.ok(savedUser);
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
