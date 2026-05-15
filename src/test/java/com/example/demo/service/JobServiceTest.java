package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Job;
import com.example.demo.model.JobType;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;

import com.example.demo.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    
    @Mock
    private JobRepository jobRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private JobService jobService;
    
    private User recruiter;
    private User candidate;
    private Job job;
    
    @BeforeEach
    void setUp() {
        recruiter = new User();
        recruiter.setId(1L);
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRole(Role.RECRUITER);
        
        candidate = new User();
        candidate.setId(2L);
        candidate.setEmail("candidate@test.com");
        candidate.setRole(Role.CANDIDATE);
        
        job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
        job.setCompany("TCS");
        job.setLocation("Bangalore");
        job.setJobType(JobType.FULL_TIME);
        job.setRecruiter(recruiter);
    }
    
    @Test
    void testCreateJob_Success() {
        // Given
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.save(any(Job.class))).thenReturn(job);
        
        // When
        Job result = jobService.createJob(job, "recruiter@test.com");
        
        // Then
        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        assertEquals(recruiter, result.getRecruiter());
        
        verify(userRepository, times(1)).findByEmail("recruiter@test.com");
        verify(jobRepository, times(1)).save(any(Job.class));
    }
    
    @Test
    void testCreateJob_OnlyRecruiterCanPost() {
        // Given
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobService.createJob(job, "candidate@test.com");
        });
        
        assertEquals("Only recruiters can post jobs", exception.getMessage());
        verify(jobRepository, never()).save(any(Job.class));
    }
    
    @Test
    void testGetAllJobs() {
        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Frontend Developer");

        Page<Job> mockPage = new PageImpl<>(Arrays.asList(job, job2));

        when(jobRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Job> result = jobService.getAllJobs(0, 2);

        assertEquals(2, result.getTotalElements());
        assertEquals("Java Developer", result.getContent().get(0).getTitle());
        assertEquals("Frontend Developer", result.getContent().get(1).getTitle());

        verify(jobRepository, times(1)).findAll(any(Pageable.class));
    }
    @Test
    void testGetJobById_Success() {
        // Given
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        // When
        Job result = jobService.getJobById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        
        verify(jobRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetJobById_NotFound() {
        // Given
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobService.getJobById(999L);
        });
        
        assertEquals("Job not found with id: 999", exception.getMessage());
    }
    
    @Test
    void testUpdateJob_Success() {
        // Given
        Job updatedJob = new Job();
        updatedJob.setTitle("Senior Java Developer");
        updatedJob.setCompany("TCS");
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.save(any(Job.class))).thenReturn(job);
        
        // When
        Job result = jobService.updateJob(1L, updatedJob, "recruiter@test.com");
        
        // Then
        assertNotNull(result);
        verify(jobRepository, times(1)).save(any(Job.class));
    }
    
    @Test
    void testUpdateJob_OnlyOwnerCanUpdate() {
        // Given
        User anotherRecruiter = new User();
        anotherRecruiter.setId(3L);
        anotherRecruiter.setEmail("another@test.com");
        anotherRecruiter.setRole(Role.RECRUITER);
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail("another@test.com")).thenReturn(Optional.of(anotherRecruiter));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobService.updateJob(1L, job, "another@test.com");
        });
        
        assertEquals("You can only update your own jobs", exception.getMessage());
        verify(jobRepository, never()).save(any(Job.class));
    }
    
    @Test
    void testDeleteJob_Success() {
        // Given
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        doNothing().when(jobRepository).deleteById(1L);
        
        // When
        jobService.deleteJob(1L, "recruiter@test.com");
        
        // Then
        verify(jobRepository, times(1)).deleteById(1L);
    }
}