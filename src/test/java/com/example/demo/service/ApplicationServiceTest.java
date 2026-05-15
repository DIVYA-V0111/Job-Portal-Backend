package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Application;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Job;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JobRepository jobRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private ApplicationService applicationService;
    
    private User candidate;
    private User recruiter;
    private Job job;
    private Application application;
    
    @BeforeEach
    void setUp() {
        candidate = new User();
        candidate.setId(1L);
        candidate.setEmail("candidate@test.com");
        candidate.setRole(Role.CANDIDATE);
        
        recruiter = new User();
        recruiter.setId(2L);
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRole(Role.RECRUITER);
        
        job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
        job.setRecruiter(recruiter);
        
        application = new Application();
        application.setId(1L);
        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(ApplicationStatus.PENDING);
    }
    
    @Test
    void testApplyForJob_Success() {
        // Given
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByCandidateAndJob(candidate, job)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        
        // When
        Application result = applicationService.applyForJob(1L, "I'm interested", "candidate@test.com");
        
        // Then
        assertNotNull(result);
        assertEquals(ApplicationStatus.PENDING, result.getStatus());
        
        verify(applicationRepository, times(1)).save(any(Application.class));
    }
    
    @Test
    void testApplyForJob_OnlyCandidateCanApply() {
        // Given
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.applyForJob(1L, "Cover letter", "recruiter@test.com");
        });
        
        assertEquals("Only candidates can apply for jobs", exception.getMessage());
        verify(applicationRepository, never()).save(any(Application.class));
    }
    
    @Test
    void testApplyForJob_AlreadyApplied() {
        // Given
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByCandidateAndJob(candidate, job)).thenReturn(Optional.of(application));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.applyForJob(1L, "Cover letter", "candidate@test.com");
        });
        
        assertEquals("You have already applied for this job", exception.getMessage());
        verify(applicationRepository, never()).save(any(Application.class));
    }
    
    @Test
    void testUpdateApplicationStatus_Success() {
        // Given
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        
        // When
        Application result = applicationService.updateApplicationStatus(1L, "ACCEPTED", "recruiter@test.com");
        
        // Then
        assertNotNull(result);
        verify(applicationRepository, times(1)).save(any(Application.class));
    }
    
    @Test
    void testDeleteApplication_Success() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        doNothing().when(applicationRepository).deleteById(1L);
        
        // When
        applicationService.deleteApplication(1L, "candidate@test.com");
        
        // Then
        verify(applicationRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteApplication_CannotDeleteAccepted() {
        // Given
        application.setStatus(ApplicationStatus.ACCEPTED);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.deleteApplication(1L, "candidate@test.com");
        });
        
        assertEquals("Cannot delete accepted applications", exception.getMessage());
        verify(applicationRepository, never()).deleteById(anyLong());
    }
}