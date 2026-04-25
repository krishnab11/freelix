package com.freelix.service;

import com.freelix.entity.Payment;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.NotificationType;
import com.freelix.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    public Payment processPayment(Project project, User client) {
        if (paymentRepository.findByProject(project).isPresent()) {
            throw new RuntimeException("Project already paid");
        }
        User freelancer = project.getSelectedFreelancer();
        if (freelancer == null) {
            throw new RuntimeException("No freelancer assigned to this project");
        }
        Payment payment = new Payment();
        payment.setProject(project);
        payment.setClient(client);
        payment.setFreelancer(freelancer);
        payment.setAmount(project.getBudget());
        payment.setStatus("COMPLETED");
        payment.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setPaidAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        projectService.markPaid(project.getId());
        emailService.sendPaymentNotification(client, freelancer, project, saved.getAmount());
        // In-app payment notification for freelancer
        notificationService.create(
                freelancer,
                "Payment of $" + String.format("%.2f", saved.getAmount()) + " received for \"" + project.getTitle() + "\" 💰",
                NotificationType.PAYMENT_RELEASED,
                "/freelancer/applications"
        );
        return saved;
    }

    public Optional<Payment> findByProject(Project project) {
        return paymentRepository.findByProject(project);
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> findByClient(User client) {
        return paymentRepository.findByClientOrderByPaidAtDesc(client);
    }

    public List<Payment> findByFreelancer(User freelancer) {
        return paymentRepository.findByFreelancerOrderByPaidAtDesc(freelancer);
    }

    public Double getTotalRevenue() {
        Double total = paymentRepository.getTotalRevenue();
        return total != null ? total : 0.0;
    }

    public long getTotalPayments() {
        return paymentRepository.count();
    }
}
