package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApplicationRequest;
import com.example.demo.model.Application;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.service.ApplicationService;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {
    
    @Autowired
    private ApplicationService applicationService;
    
    // Apply for a job
 // Update your controller
    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyForJob(
            @PathVariable Long jobId,
            @RequestBody ApplicationRequest request,  // Changed this
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Application application = applicationService.applyForJob(
                jobId, 
                request.getCoverLetter(),  // Changed this
                email
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Get my applications (candidate)
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<Application> applications = applicationService.getCandidateApplications(email);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Get applications for a job (recruiter)
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJobApplications(
            @PathVariable Long jobId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            List<Application> applications = applicationService.getJobApplications(jobId, email);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Update application status (recruiter)
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Application application = applicationService.updateApplicationStatus(applicationId, status, email);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Get application by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            Application application = applicationService.getApplicationById(id);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<?> deleteApplication(
            @PathVariable Long applicationId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            applicationService.deleteApplication(applicationId, email);
            return ResponseEntity.ok("Application deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
            
        }
       
    }
    
}