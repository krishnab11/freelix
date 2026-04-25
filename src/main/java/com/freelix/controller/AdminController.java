package com.freelix.controller;

import com.freelix.service.AdminService;
import com.freelix.service.ProjectService;
import com.freelix.service.UserService;
import com.freelix.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AdminService adminService;
    @Autowired private UserService userService;
    @Autowired private ProjectService projectService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        Map<String, Object> stats = adminService.getDashboardStats();
        model.addAttribute("user", ud.getUser());
        model.addAllAttributes(stats);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        model.addAttribute("user", ud.getUser());
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/projects")
    public String projects(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        model.addAttribute("user", ud.getUser());
        model.addAttribute("projects", projectService.findAll());
        return "admin/projects";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.findById(id).ifPresent(u -> {
            // Only delete non-admin users
            if (!u.getRole().name().equals("ADMIN")) {
                userService.save(u); // placeholder - soft delete would be here
            }
        });
        return "redirect:/admin/users";
    }
}
