package com.freelix.service;

import com.freelix.dto.ApplicationDto;
import com.freelix.entity.Application;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.ApplicationStatus;
import com.freelix.enums.NotificationType;
import com.freelix.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    public Application apply(ApplicationDto dto, User freelancer, Project project) {
        if (applicationRepository.existsByFreelancerAndProject(freelancer, project)) {
            throw new RuntimeException("You have already applied to this project");
        }
        Application app = new Application();
        app.setProposal(dto.getProposal());
        app.setBidAmount(dto.getBidAmount());
        app.setEstimatedDays(dto.getEstimatedDays());
        app.setFreelancer(freelancer);
        app.setProject(project);
        app.setStatus(ApplicationStatus.PENDING);
        Application saved = applicationRepository.save(app);
        emailService.sendApplicationNotification(project.getClient(), freelancer, project);
        // In-app notification for the client
        notificationService.create(
                project.getClient(),
                freelancer.getName() + " applied for \"" + project.getTitle() + "\"",
                NotificationType.NEW_APPLICATION,
                "/client/projects/" + project.getId() + "/applicants"
        );
        return saved;
    }

    public Application accept(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(app);

        // Reject all other applications for this project
        List<Application> others = applicationRepository.findByProject(app.getProject());
        for (Application other : others) {
            if (!other.getId().equals(applicationId)) {
                other.setStatus(ApplicationStatus.REJECTED);
                applicationRepository.save(other);
            }
        }
        projectService.setFreelancer(app.getProject().getId(), app.getFreelancer());
        emailService.sendAssignmentNotification(app.getFreelancer(), app.getProject());
        // In-app notification for the freelancer
        notificationService.create(
                app.getFreelancer(),
                "Your application for \"" + app.getProject().getTitle() + "\" was accepted! 🎉",
                NotificationType.APPLICATION_ACCEPTED,
                "/freelancer/applications"
        );
        return app;
    }

    public Application reject(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(ApplicationStatus.REJECTED);
        Application saved = applicationRepository.save(app);
        // In-app notification for the freelancer
        notificationService.create(
                app.getFreelancer(),
                "Your application for \"" + app.getProject().getTitle() + "\" was not selected.",
                NotificationType.APPLICATION_REJECTED,
                "/freelancer/applications"
        );
        return saved;
    }

    public List<Application> findByFreelancer(User freelancer) {
        return applicationRepository.findByFreelancerOrderByCreatedAtDesc(freelancer);
    }

    public List<Application> findByProject(Project project) {
        return applicationRepository.findByProject(project);
    }

    public Optional<Application> findByFreelancerAndProject(User freelancer, Project project) {
        return applicationRepository.findByFreelancerAndProject(freelancer, project);
    }

    public boolean hasApplied(User freelancer, Project project) {
        return applicationRepository.existsByFreelancerAndProject(freelancer, project);
    }
}
