package com.example.demo.controller;

import com.example.demo.model.SavedJob;
import com.example.demo.service.SavedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
@CrossOrigin(origins = "*")
public class SavedJobController {

    @Autowired private SavedJobService savedJobService;

    @PostMapping("/{jobId}")
    public ResponseEntity<?> saveJob(@PathVariable Long jobId, Authentication authentication) {
        try {
            SavedJob saved = savedJobService.saveJob(jobId, authentication.getName());
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> unsaveJob(@PathVariable Long jobId, Authentication authentication) {
        try {
            savedJobService.unsaveJob(jobId, authentication.getName());
            return ResponseEntity.ok("Job removed from saved list");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getSavedJobs(Authentication authentication) {
        try {
            List<SavedJob> jobs = savedJobService.getMySavedJobs(authentication.getName());
            return ResponseEntity.ok(jobs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}