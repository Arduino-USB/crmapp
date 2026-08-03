package com.crm.controllers;

import com.crm.embeds.*;

import com.crm.repos.UserRepo;
import com.crm.entites.User;
import com.crm.EntityMerge;

import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
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
    public User createUser (@Valid @RequestBody User user) {
        if (user.getId() == null) {
            // Find the maximum existing ID to auto-increment safely, or let JPA handle generation if configured with GenerationType.IDENTITY
            Long maxId = userRepo.findAll().stream().mapToLong(User::getId).max().orElse(0L);
            user.setId(maxId + 1);
        }
        User savedUser = userRepo.save(user);    
        return user;
    }
    
    @GetMapping("/get_all_users")
    public Page<User> getAllUsers(@RequestParam int page, @RequestParam(defaultValue="9") int pageSize) {
        return userRepo.findAll(PageRequest.of(page, pageSize));        
    }

    @PutMapping("/update_user")
    public User updateUser(@RequestBody User updatedUserDetails) {

        Optional<User> originalUser = userRepo.findById(updatedUserDetails.getId());

        if (!originalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        User merged = EntityMerge.merge(originalUser.get(), updatedUserDetails);

        return userRepo.save(merged);
    }

    @GetMapping("/search")
    public Page<User> searchUsersByPartName(@RequestParam String partName) {
        // Pageable.unpaged() fetches all matching records and packs them into a single Page response
        return userRepo.findByNameContainingIgnoreCase(partName, Pageable.unpaged());
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<String> deleteUser(@RequestParam Long id) {
        if (!userRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
        
        userRepo.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
