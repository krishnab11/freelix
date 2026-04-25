package com.freelix.repository;

import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByClient(User client);
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findBySelectedFreelancer(User freelancer);
    List<Project> findByClientOrderByCreatedAtDesc(User client);
    List<Project> findByStatusOrderByCreatedAtDesc(ProjectStatus status);
    long countByStatus(ProjectStatus status);
    long countByClient(User client);
}
