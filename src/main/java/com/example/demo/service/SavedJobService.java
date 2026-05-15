package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SavedJobService {

    @Autowired private SavedJobRepository savedJobRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;

    public SavedJob saveJob(Long jobId, String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (savedJobRepository.findByCandidateAndJob(candidate, job).isPresent()) {
            throw new RuntimeException("Job already saved");
        }

        SavedJob savedJob = new SavedJob();
        savedJob.setCandidate(candidate);
        savedJob.setJob(job);
        return savedJobRepository.save(savedJob);
    }

    public void unsaveJob(Long jobId, String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        SavedJob savedJob = savedJobRepository.findByCandidateAndJob(candidate, job)
                .orElseThrow(() -> new RuntimeException("Saved job not found"));
        savedJobRepository.delete(savedJob);
    }

    public List<SavedJob> getMySavedJobs(String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return savedJobRepository.findByCandidate(candidate);
    }
}