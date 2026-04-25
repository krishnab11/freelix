package com.freelix.service;

import com.freelix.dto.ProjectDto;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.ProjectStatus;
import com.freelix.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project create(ProjectDto dto, User client) {
        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setBudget(dto.getBudget());
        project.setCategory(dto.getCategory());
        project.setSkills(dto.getSkills());
        project.setDeadline(dto.getDeadline());
        project.setClient(client);
        project.setStatus(ProjectStatus.OPEN);
        return projectRepository.save(project);
    }

    public Project update(Long id, ProjectDto dto, User client) {
        Project project = getByIdAndClient(id, client);
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setBudget(dto.getBudget());
        project.setCategory(dto.getCategory());
        project.setSkills(dto.getSkills());
        project.setDeadline(dto.getDeadline());
        return projectRepository.save(project);
    }

    public void delete(Long id, User client) {
        Project project = getByIdAndClient(id, client);
        projectRepository.delete(project);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> findByClient(User client) {
        return projectRepository.findByClientOrderByCreatedAtDesc(client);
    }

    public List<Project> findOpenProjects() {
        return projectRepository.findByStatusOrderByCreatedAtDesc(ProjectStatus.OPEN);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public List<Project> findByFreelancer(User freelancer) {
        return projectRepository.findBySelectedFreelancer(freelancer);
    }

    public Project setFreelancer(Long projectId, User freelancer) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setSelectedFreelancer(freelancer);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        return projectRepository.save(project);
    }

    public Project markCompleted(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setStatus(ProjectStatus.COMPLETED);
        return projectRepository.save(project);
    }

    public Project markPaid(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setStatus(ProjectStatus.PAID);
        return projectRepository.save(project);
    }

    public long countByStatus(ProjectStatus status) {
        return projectRepository.countByStatus(status);
    }

    private Project getByIdAndClient(Long id, User client) {
        return projectRepository.findById(id)
                .filter(p -> p.getClient().getId().equals(client.getId()))
                .orElseThrow(() -> new RuntimeException("Project not found or not authorized"));
    }
}
