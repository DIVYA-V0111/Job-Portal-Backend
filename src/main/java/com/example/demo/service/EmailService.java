package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Generic send method
    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // Send when application is submitted
    public void sendApplicationConfirmation(String toEmail, String candidateName, String jobTitle, String company) {
        String subject = "Application Submitted - " + jobTitle;
        String body = "Hi " + candidateName + ",\n\n"
                + "Your application for " + jobTitle + " at " + company + " has been submitted successfully.\n\n"
                + "We will notify you when there is an update.\n\n"
                + "Best regards,\nJob Portal Team";
        sendEmail(toEmail, subject, body);
    }

    // Send when application status is updated
    public void sendStatusUpdateEmail(String toEmail, String candidateName, String jobTitle, String status) {
        String subject = "Application Status Update - " + jobTitle;
        String body = "Hi " + candidateName + ",\n\n"
                + "Your application status for " + jobTitle + " has been updated to: " + status + "\n\n"
                + "Login to your account to check more details.\n\n"
                + "Best regards,\nJob Portal Team";
        sendEmail(toEmail, subject, body);
    }

    // Send job alert to candidate
    public void sendJobAlert(String toEmail, String candidateName, String jobTitle, String company, String location) {
        String subject = "New Job Alert - " + jobTitle;
        String body = "Hi " + candidateName + ",\n\n"
                + "A new job matching your profile has been posted!\n\n"
                + "Job Title  : " + jobTitle + "\n"
                + "Company    : " + company + "\n"
                + "Location   : " + location + "\n\n"
                + "Login to your account to view and apply.\n\n"
                + "Best regards,\nJob Portal Team";
        sendEmail(toEmail, subject, body);
    }
}