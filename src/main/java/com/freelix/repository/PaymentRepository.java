package com.freelix.repository;

import com.freelix.entity.Payment;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByProject(Project project);
    List<Payment> findByClientOrderByPaidAtDesc(User client);
    List<Payment> findByFreelancerOrderByPaidAtDesc(User freelancer);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p")
    Double getTotalRevenue();

    long count();
}
