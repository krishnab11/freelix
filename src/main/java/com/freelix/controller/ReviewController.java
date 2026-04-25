package com.freelix.controller;

import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.security.CustomUserDetails;
import com.freelix.service.ProjectService;
import com.freelix.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired private ReviewService reviewService;
    @Autowired private ProjectService projectService;

    @PostMapping("/submit")
    public String submitReview(@RequestParam Long projectId,
                               @RequestParam int rating,
                               @RequestParam(required = false) String feedback,
                               @AuthenticationPrincipal CustomUserDetails ud,
                               RedirectAttributes ra) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User freelancer = project.getSelectedFreelancer();
        if (freelancer == null) {
            ra.addFlashAttribute("error", "No freelancer assigned to review.");
            return "redirect:/client/projects/" + projectId + "/applicants";
        }
        try {
            reviewService.submitReview(rating, feedback, ud.getUser(), freelancer, project);
            ra.addFlashAttribute("success", "Review submitted! Thank you.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/client/projects/" + projectId + "/applicants";
    }
}
