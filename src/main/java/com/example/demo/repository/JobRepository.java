package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Job;
import com.example.demo.model.User;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findByRecruiter(User recruiter);
    
    List<Job> findByLocation(String location);
    
    List<Job> findByCompany(String company);
    
    List<Job> findByTitleContainingIgnoreCase(String keyword);
    
    @Query("SELECT j FROM Job j WHERE " +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Job> searchJobs(@Param("keyword") String keyword);
    
 
    @Query("SELECT j FROM Job j WHERE " +
           "LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Job> findBySkill(@Param("skill") String skill);
}