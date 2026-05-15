package com.example.demo.service;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendedJobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Job> getRecommendedJobs(String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (candidate.getSkills() == null || candidate.getSkills().isEmpty()) {
            throw new RuntimeException("Please update your skills in profile first");
        }

        // Split candidate skills by comma
        String[] skills = candidate.getSkills().split(",");

        List<Job> recommendedJobs = new ArrayList<>();

        for (String skill : skills) {
            String trimmedSkill = skill.trim().toLowerCase();

            // Get all jobs and filter by exact skill match
            List<Job> allJobs = jobRepository.findAll();
            for (Job job : allJobs) {
                if (job.getSkillsRequired() != null) {
                    // Split job skills and check exact match
                    String[] jobSkills = job.getSkillsRequired().split(",");
                    for (String jobSkill : jobSkills) {
                        if (jobSkill.trim().toLowerCase().equals(trimmedSkill)) {
                            if (!recommendedJobs.contains(job)) {
                                recommendedJobs.add(job);
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (recommendedJobs.isEmpty()) {
            throw new RuntimeException("No jobs found matching your skills");
        }

        return recommendedJobs;
    }
}