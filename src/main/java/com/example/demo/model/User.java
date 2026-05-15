package com.example.demo.model;

import java.time.LocalDateTime;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;
	
	private String phone;
	
	private String location;
	
	@Column(name = "created_at", nullable = false,updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "resume_path")
	private String resumePath;
	
	@Column(name = "otp")
	private String otp;

	@Column(name = "otp_expiry")
	private LocalDateTime otpExpiry;
	
	@Column(name = "skills", length = 1000)
	private String skills;
	
	// Default constructor
	public User() {
		this.createdAt = LocalDateTime.now();
	}
	
	// Parameterized constructor
	public User(String name, String email, String password, Role role) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.createdAt = LocalDateTime.now();
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getResumePath() {
	    return resumePath;
	}
	public void setResumePath(String resumePath) {
	    this.resumePath = resumePath;
	}
	
	public String getOtp() {
	    return otp;
	}
	public void setOtp(String otp) {
	    this.otp = otp;
	}
	public LocalDateTime getOtpExpiry() {
	    return otpExpiry;
	}
	public void setOtpExpiry(LocalDateTime otpExpiry) {
	    this.otpExpiry = otpExpiry;
	}
	public String getSkills() {
	    return skills;
	}
	public void setSkills(String skills) {
	    this.skills = skills;
	}
}
