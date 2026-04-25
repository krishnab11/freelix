package com.freelix.service;

import com.freelix.enums.ProjectStatus;
import com.freelix.enums.Role;
import com.freelix.repository.PaymentRepository;
import com.freelix.repository.ProjectRepository;
import com.freelix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalClients", userRepository.countByRole(Role.CLIENT));
        stats.put("totalFreelancers", userRepository.countByRole(Role.FREELANCER));
        stats.put("totalProjects", projectRepository.count());
        stats.put("openProjects", projectRepository.countByStatus(ProjectStatus.OPEN));
        stats.put("inProgressProjects", projectRepository.countByStatus(ProjectStatus.IN_PROGRESS));
        stats.put("completedProjects", projectRepository.countByStatus(ProjectStatus.COMPLETED));
        stats.put("paidProjects", projectRepository.countByStatus(ProjectStatus.PAID));
        stats.put("totalPayments", paymentRepository.count());
        Double revenue = paymentRepository.getTotalRevenue();
        stats.put("totalRevenue", revenue != null ? revenue : 0.0);
        return stats;
    }
}
