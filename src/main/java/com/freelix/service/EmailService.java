package com.freelix.service;

import com.freelix.entity.Project;
import com.freelix.entity.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Async
    public void sendWelcomeEmail(User user) {
        // Email sending disabled - configure SMTP credentials in application.properties to enable
        System.out.println("[EMAIL] Welcome email would be sent to: " + user.getEmail());
    }

    @Async
    public void sendApplicationNotification(User client, User freelancer, Project project) {
        System.out.println("[EMAIL] Application notification would be sent to: " + client.getEmail());
    }

    @Async
    public void sendAssignmentNotification(User freelancer, Project project) {
        System.out.println("[EMAIL] Assignment notification would be sent to: " + freelancer.getEmail());
    }

    @Async
    public void sendPaymentNotification(User client, User freelancer, Project project, Double amount) {
        System.out.println("[EMAIL] Payment notification would be sent to: " + freelancer.getEmail());
    }
}
