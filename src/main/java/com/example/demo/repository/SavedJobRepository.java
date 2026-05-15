package com.example.demo.repository;

import com.example.demo.model.SavedJob;
import com.example.demo.model.User;
import com.example.demo.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByCandidate(User candidate);
    Optional<SavedJob> findByCandidateAndJob(User candidate, Job job);
}