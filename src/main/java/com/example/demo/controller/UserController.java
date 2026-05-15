package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
	    User savedUser = userService.saveUser(user);
	    return new ResponseEntity<>(savedUser, HttpStatus.CREATED); 
	}
	
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users,HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User>getUserById(@PathVariable Long id){
		User user = userService.getUserById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	// Update own profile
	@PutMapping("/profile")
	public ResponseEntity<?> updateProfile(@RequestBody User updatedUser, Authentication authentication) {
	    try {
	        String email = authentication.getName();
	        User user = userService.getUserByEmail(email);
	        User saved = userService.updatedUser(user.getId(), updatedUser);
	        return ResponseEntity.ok(saved);
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

	// Get own profile
	@GetMapping("/profile")
	public ResponseEntity<?> getProfile(Authentication authentication) {
	    try {
	        String email = authentication.getName();
	        User user = userService.getUserByEmail(email);
	        return ResponseEntity.ok(user);
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

	// Change password
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(
	        @RequestParam String oldPassword,
	        @RequestParam String newPassword,
	        Authentication authentication) {
	    try {
	        String email = authentication.getName();
	        userService.changePassword(email, oldPassword, newPassword);
	        return ResponseEntity.ok("Password updated successfully");
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

}
