package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@Service
public class ResumeService {

    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Upload resume
    public String uploadResume(MultipartFile file, String email) throws IOException {

        // Only allow PDF
        if (!file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        // Get user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create upload folder if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file with user ID as filename to avoid conflicts
        String fileName = "resume_" + user.getId() + ".pdf";
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save path in user record
        user.setResumePath(filePath.toString());
        userRepository.save(user);

        return fileName;
    }

    // Download resume
    public Resource downloadResume(String email) throws MalformedURLException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getResumePath() == null) {
            throw new RuntimeException("No resume uploaded yet");
        }

        Path filePath = Paths.get(user.getResumePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Resume file not found");
        }

        return resource;
    }

    // Delete resume
    public void deleteResume(String email) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getResumePath() == null) {
            throw new RuntimeException("No resume to delete");
        }

        // Delete file from disk
        Path filePath = Paths.get(user.getResumePath());
        Files.deleteIfExists(filePath);

        // Remove path from user record
        user.setResumePath(null);
        userRepository.save(user);
    }
}