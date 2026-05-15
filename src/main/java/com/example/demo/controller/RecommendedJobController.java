package com.example.demo.controller;

import com.example.demo.model.Job;
import com.example.demo.service.RecommendedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommended-jobs")
@CrossOrigin(origins = "*")
public class RecommendedJobController {

    @Autowired
    private RecommendedJobService recommendedJobService;

    @GetMapping
    public ResponseEntity<?> getRecommendedJobs(Authentication authentication) {
        try {
            List<Job> jobs = recommendedJobService.getRecommendedJobs(
                    authentication.getName());
            return ResponseEntity.ok(jobs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}