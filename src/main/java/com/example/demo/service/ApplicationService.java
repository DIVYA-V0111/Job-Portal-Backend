package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Application;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Job;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ApplicationService {
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private EmailService emailService;
    
    public Application applyForJob(Long jobId, String coverLetter, String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (candidate.getRole() != Role.CANDIDATE) {
            throw new RuntimeException("Only candidates can apply for jobs");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.findByCandidateAndJob(candidate, job).isPresent()) {
            throw new RuntimeException("You have already applied for this job");
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setCoverLetter(coverLetter);

        Application saved = applicationRepository.save(application);

        // Send confirmation email to candidate
        emailService.sendApplicationConfirmation(
                candidate.getEmail(),
                candidate.getName(),
                job.getTitle(),
                job.getCompany()
        );

        return saved;
    }
    
    // Get all applications for a candidate
    public List<Application> getCandidateApplications(String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return applicationRepository.findByCandidate(candidate);
    }
    
    // Get all applications for a job (recruiter only)
    public List<Application> getJobApplications(Long jobId, String email) {
        // Get recruiter
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Check if recruiter owns this job
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("You can only view applications for your own jobs");
        }
        
        return applicationRepository.findByJob(job);
    }
    
    public Application updateApplicationStatus(Long applicationId, String status, String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("You can only update applications for your own jobs");
        }

        application.setStatus(ApplicationStatus.valueOf(status));
        Application updated = applicationRepository.save(application);

        // Send status update email to candidate
        emailService.sendStatusUpdateEmail(
                application.getCandidate().getEmail(),
                application.getCandidate().getName(),
                application.getJob().getTitle(),
                status
        );

        return updated;
    }
    // Get application by ID
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }
    
    public void deleteApplication(Long applicationId, String email) {
        // Get application
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Get current user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is the candidate who applied
        if (!application.getCandidate().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own applications");
        }

        // Check if application is already accepted
        if (application.getStatus() == ApplicationStatus.ACCEPTED) {
            throw new RuntimeException("Cannot delete accepted applications");
        }

        // Delete application
        applicationRepository.deleteById(applicationId);
    }
}