package com.freelix.controller;

import com.freelix.dto.ProjectDto;
import com.freelix.entity.Application;
import com.freelix.entity.Project;
import com.freelix.entity.User;
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
@RequestMapping("/client")
public class ClientController {

    @Autowired private ProjectService projectService;
    @Autowired private ApplicationService applicationService;
    @Autowired private PaymentService paymentService;
    @Autowired private ReviewService reviewService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        User client = ud.getUser();
        List<Project> projects = projectService.findByClient(client);
        model.addAttribute("user", client);
        model.addAttribute("projects", projects);
        model.addAttribute("totalProjects", projects.size());
        model.addAttribute("openProjects", projects.stream().filter(p -> p.getStatus() == ProjectStatus.OPEN).count());
        model.addAttribute("inProgressProjects", projects.stream().filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS).count());
        model.addAttribute("completedProjects", projects.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED || p.getStatus() == ProjectStatus.PAID).count());
        return "client/dashboard";
    }

    @GetMapping("/projects")
    public String projects(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        User client = ud.getUser();
        model.addAttribute("user", client);
        model.addAttribute("projects", projectService.findByClient(client));
        return "client/projects";
    }

    @GetMapping("/projects/new")
    public String newProjectForm(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        model.addAttribute("user", ud.getUser());
        model.addAttribute("projectDto", new ProjectDto());
        model.addAttribute("isEdit", false);
        return "client/project-form";
    }

    @PostMapping("/projects/new")
    public String createProject(@AuthenticationPrincipal CustomUserDetails ud,
                                @Valid @ModelAttribute("projectDto") ProjectDto dto,
                                BindingResult result,
                                RedirectAttributes ra,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", ud.getUser());
            model.addAttribute("isEdit", false);
            return "client/project-form";
        }
        projectService.create(dto, ud.getUser());
        ra.addFlashAttribute("success", "Project created successfully!");
        return "redirect:/client/projects";
    }

    @GetMapping("/projects/{id}/edit")
    public String editProjectForm(@PathVariable Long id,
                                  @AuthenticationPrincipal CustomUserDetails ud,
                                  Model model) {
        Project project = projectService.findById(id)
                .filter(p -> p.getClient().getId().equals(ud.getUser().getId()))
                .orElseThrow(() -> new RuntimeException("Project not found"));
        ProjectDto dto = new ProjectDto();
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setBudget(project.getBudget());
        dto.setCategory(project.getCategory());
        dto.setSkills(project.getSkills());
        dto.setDeadline(project.getDeadline());
        model.addAttribute("user", ud.getUser());
        model.addAttribute("projectDto", dto);
        model.addAttribute("projectId", id);
        model.addAttribute("isEdit", true);
        return "client/project-form";
    }

    @PostMapping("/projects/{id}/edit")
    public String updateProject(@PathVariable Long id,
                                @Valid @ModelAttribute("projectDto") ProjectDto dto,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails ud,
                                RedirectAttributes ra,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", ud.getUser());
            model.addAttribute("projectId", id);
            model.addAttribute("isEdit", true);
            return "client/project-form";
        }
        projectService.update(id, dto, ud.getUser());
        ra.addFlashAttribute("success", "Project updated!");
        return "redirect:/client/projects";
    }

    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails ud,
                                RedirectAttributes ra) {
        projectService.delete(id, ud.getUser());
        ra.addFlashAttribute("success", "Project deleted.");
        return "redirect:/client/projects";
    }

    @GetMapping("/projects/{id}/applicants")
    public String viewApplicants(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserDetails ud,
                                 Model model) {
        Project project = projectService.findById(id)
                .filter(p -> p.getClient().getId().equals(ud.getUser().getId()))
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<Application> applications = applicationService.findByProject(project);
        model.addAttribute("user", ud.getUser());
        model.addAttribute("project", project);
        model.addAttribute("applications", applications);
        model.addAttribute("hasReview", reviewService.hasReview(id));
        model.addAttribute("payment", paymentService.findByProject(project).orElse(null));
        return "client/applicants";
    }

    @PostMapping("/projects/{projectId}/accept/{applicationId}")
    public String acceptApplicant(@PathVariable Long projectId,
                                  @PathVariable Long applicationId,
                                  RedirectAttributes ra) {
        applicationService.accept(applicationId);
        ra.addFlashAttribute("success", "Freelancer selected and notified!");
        return "redirect:/client/projects/" + projectId + "/applicants";
    }

    @PostMapping("/projects/{projectId}/reject/{applicationId}")
    public String rejectApplicant(@PathVariable Long projectId,
                                  @PathVariable Long applicationId,
                                  RedirectAttributes ra) {
        applicationService.reject(applicationId);
        ra.addFlashAttribute("success", "Application rejected.");
        return "redirect:/client/projects/" + projectId + "/applicants";
    }
}
