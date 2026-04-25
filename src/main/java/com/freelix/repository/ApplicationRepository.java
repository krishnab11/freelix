package com.freelix.repository;

import com.freelix.entity.Application;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByFreelancer(User freelancer);
    List<Application> findByProject(Project project);
    List<Application> findByFreelancerOrderByCreatedAtDesc(User freelancer);
    Optional<Application> findByFreelancerAndProject(User freelancer, Project project);
    boolean existsByFreelancerAndProject(User freelancer, Project project);
    long countByFreelancerAndStatus(User freelancer, ApplicationStatus status);
    long countByStatus(ApplicationStatus status);
}
