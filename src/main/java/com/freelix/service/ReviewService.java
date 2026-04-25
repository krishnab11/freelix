package com.freelix.service;

import com.freelix.entity.Project;
import com.freelix.entity.Review;
import com.freelix.entity.User;
import com.freelix.repository.ReviewRepository;
import com.freelix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    public Review submitReview(int rating, String feedback, User client, User freelancer, Project project) {
        if (reviewRepository.existsByProject_Id(project.getId())) {
            throw new RuntimeException("Review already submitted for this project");
        }
        Review review = new Review();
        review.setRating(rating);
        review.setFeedback(feedback);
        review.setClient(client);
        review.setFreelancer(freelancer);
        review.setProject(project);
        Review saved = reviewRepository.save(review);
        recalculateRating(freelancer);
        return saved;
    }

    private void recalculateRating(User freelancer) {
        Double avg = reviewRepository.getAverageRatingByFreelancer(freelancer);
        long count = reviewRepository.countByFreelancer(freelancer);
        freelancer.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        freelancer.setTotalReviews((int) count);
        userRepository.save(freelancer);
    }

    public List<Review> findByFreelancer(User freelancer) {
        return reviewRepository.findByFreelancerOrderByCreatedAtDesc(freelancer);
    }

    public Optional<Review> findByProjectId(Long projectId) {
        return reviewRepository.findByProject_Id(projectId);
    }

    public boolean hasReview(Long projectId) {
        return reviewRepository.existsByProject_Id(projectId);
    }
}
