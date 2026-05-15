package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.Job;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    public Job createJob(Job job, String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (recruiter.getRole() != Role.RECRUITER) {
            throw new RuntimeException("Only recruiters can post jobs");
        }

        job.setRecruiter(recruiter);
        Job savedJob = jobRepository.save(job);

        // Send job alert to all candidates
        List<User> candidates = userRepository.findByRole(Role.CANDIDATE);
        candidates.forEach(candidate ->
            emailService.sendJobAlert(
                    candidate.getEmail(),
                    candidate.getName(),
                    savedJob.getTitle(),
                    savedJob.getCompany(),
                    savedJob.getLocation()
            )
        );

        return savedJob;
    }
    public Page<Job> getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedDate").descending());
        return jobRepository.findAll(pageable);
    }
    
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }
    
    public Job updateJob(Long id, Job updatedJob, String email) {
        Job existing = getJobById(id);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!existing.getRecruiter().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own jobs");
        }
        
        existing.setTitle(updatedJob.getTitle());
        existing.setDescription(updatedJob.getDescription());
        existing.setCompany(updatedJob.getCompany());
        existing.setLocation(updatedJob.getLocation());
        existing.setSalary(updatedJob.getSalary());
        existing.setExperienceRequired(updatedJob.getExperienceRequired());
        existing.setSkillsRequired(updatedJob.getSkillsRequired());
        existing.setJobType(updatedJob.getJobType());
        
        return jobRepository.save(existing);
    }
    
    public void deleteJob(Long id, String email) {
        Job job = getJobById(id);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!job.getRecruiter().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own jobs");
        }
        
        jobRepository.deleteById(id);
    }
    
    public List<Job> searchJobs(String keyword) {
        return jobRepository.searchJobs(keyword);
    }
    
    public List<Job> getJobsByRecruiter(String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return jobRepository.findByRecruiter(recruiter);
    }
}