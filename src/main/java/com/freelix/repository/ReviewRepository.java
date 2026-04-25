package com.freelix.repository;

import com.freelix.entity.Review;
import com.freelix.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFreelancerOrderByCreatedAtDesc(User freelancer);
    Optional<Review> findByProject_Id(Long projectId);
    boolean existsByProject_Id(Long projectId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.freelancer = :freelancer")
    Double getAverageRatingByFreelancer(@Param("freelancer") User freelancer);

    long countByFreelancer(User freelancer);
}
