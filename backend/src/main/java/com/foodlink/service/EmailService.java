package com.foodlink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false) // required=false so app boots even if mail isn't perfectly configured
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String configuredUsername;

    public void sendEmail(String to, String subject, String text) {
        if (mailSender == null || configuredUsername == null || configuredUsername.isEmpty() || configuredUsername.equals("apikey")) {
            log.warn("Email service not configured. Mocking email to {}. Subject: {}", to, subject);
            log.info("MOCK EMAIL BODY:\n{}", text);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@foodlink.org");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Successfully sent email to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}
