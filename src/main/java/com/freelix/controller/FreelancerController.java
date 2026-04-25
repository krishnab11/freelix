package com.freelix.controller;

import com.freelix.dto.ApplicationDto;
import com.freelix.entity.Application;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.ApplicationStatus;
import com.freelix.enums.ProjectStatus;
import com.freelix.security.CustomUserDetails;
import com.freelix.service.ApplicationService;
import com.freelix.service.PaymentService;
import com.freelix.service.ProjectService;
import com.freelix.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/freelancer")
public class FreelancerController {

    @Autowired private ProjectService projectService;
    @Autowired private ApplicationService applicationService;
    @Autowired private ReviewService reviewService;
    @Autowired private PaymentService paymentService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        User freelancer = ud.getUser();
        List<Application> myApps = applicationService.findByFreelancer(freelancer);
        List<Project> myProjects = projectService.findByFreelancer(freelancer);
        model.addAttribute("user", freelancer);
        model.addAttribute("totalApplications", myApps.size());
        model.addAttribute("pendingApplications", myApps.stream().filter(a -> a.getStatus() == ApplicationStatus.PENDING).count());
        model.addAttribute("acceptedApplications", myApps.stream().filter(a -> a.getStatus() == ApplicationStatus.ACCEPTED).count());
        model.addAttribute("activeProjects", myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS).count());
        model.addAttribute("completedProjects", myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.PAID).count());
        model.addAttribute("recentApplications", myApps.stream().limit(5).toList());
        model.addAttribute("reviews", reviewService.findByFreelancer(freelancer));
        return "freelancer/dashboard";
    }

    @GetMapping("/browse")
    public String browseProjects(@AuthenticationPrincipal CustomUserDetails ud,
                                 @RequestParam(required = false) String category,
                                 Model model) {
        User freelancer = ud.getUser();
        List<Project> openProjects = projectService.findOpenProjects();
        // Build a set of project IDs that this freelancer has already applied to
        java.util.Set<Long> appliedIds = openProjects.stream()
                .filter(p -> applicationService.hasApplied(freelancer, p))
                .map(Project::getId)
                .collect(java.util.stream.Collectors.toSet());
        model.addAttribute("user", freelancer);
        model.addAttribute("projects", openProjects);
        model.addAttribute("appliedIds", appliedIds);
        return "freelancer/browse";
    }

    @GetMapping("/projects/{id}/apply")
    public String applyForm(@PathVariable Long id,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            Model model) {
        Project project = projectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (applicationService.hasApplied(ud.getUser(), project)) {
            return "redirect:/freelancer/browse";
        }
        model.addAttribute("user", ud.getUser());
        model.addAttribute("project", project);
        model.addAttribute("applicationDto", new ApplicationDto());
        return "freelancer/apply";
    }

    @PostMapping("/projects/{id}/apply")
    public String submitApplication(@PathVariable Long id,
                                    @Valid @ModelAttribute("applicationDto") ApplicationDto dto,
                                    BindingResult result,
                                    @AuthenticationPrincipal CustomUserDetails ud,
                                    RedirectAttributes ra,
                                    Model model) {
        Project project = projectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (result.hasErrors()) {
            model.addAttribute("user", ud.getUser());
            model.addAttribute("project", project);
            return "freelancer/apply";
        }
        try {
            applicationService.apply(dto, ud.getUser(), project);
            ra.addFlashAttribute("success", "Application submitted successfully!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/freelancer/applications";
    }

    @GetMapping("/applications")
    public String myApplications(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        User freelancer = ud.getUser();
        model.addAttribute("user", freelancer);
        model.addAttribute("applications", applicationService.findByFreelancer(freelancer));
        return "freelancer/applications";
    }
}
