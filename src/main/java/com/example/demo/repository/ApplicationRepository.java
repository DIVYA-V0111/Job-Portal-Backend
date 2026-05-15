package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Application;
import com.example.demo.model.Job;
import com.example.demo.model.User;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    // Find all applications by candidate
    List<Application> findByCandidate(User candidate);
    
    // Find all applications for a specific job
    List<Application> findByJob(Job job);
    
    // Check if candidate already applied for this job
    Optional<Application> findByCandidateAndJob(User candidate, Job job);
    
    // Find applications by status
    List<Application> findByStatus(com.example.demo.model.ApplicationStatus status);
}