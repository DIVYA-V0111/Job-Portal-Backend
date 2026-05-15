package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepositary;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// CREATE - Register a new User
	public User saveUser(User user) {
		return userRepositary.save(user);
	}

	// READ ALL - Get all User
	public List<User> getAllUsers() {
		return userRepositary.findAll();
	}

	// READ BY ID - Get User by ID
	public User getUserById(Long id) {
		return userRepositary.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	// UPDATE - Update existing User
	public User updatedUser(Long id, User updatedUser) {
		User existing = getUserById(id);

		existing.setName(updatedUser.getName());
		existing.setEmail(updatedUser.getEmail());
		existing.setLocation(updatedUser.getLocation());
		existing.setPhone(updatedUser.getPhone());
		existing.setSkills(updatedUser.getSkills());

		// DO NOT update password and role here
		// password is updated only via changePassword method
		// role should never change after registration

		return userRepositary.save(existing);
	}

	// DELETE - Delete User by ID
	public void deleteUser(Long id) {
		getUserById(id);
		userRepositary.deleteById(id);
	}

	// FIND BY EMAIL - useful for login
	public User getUserByEmail(String email) {
		return userRepositary.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	// FIND BY ROLE - get all users with a specific role
	public List<User> getAllUsersByRole(Role role) {
		return userRepositary.findByRole(role);
	}

	public void changePassword(String email, String oldPassword, String newPassword) {
		User user = getUserByEmail(email);
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new RuntimeException("Old password is incorrect");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepositary.save(user);
	}
}
