package com.freelix.controller;

import com.freelix.security.CustomUserDetails;
import com.freelix.enums.Role;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Role role = userDetails.getUser().getRole();
        return switch (role) {
            case CLIENT -> "redirect:/client/dashboard";
            case FREELANCER -> "redirect:/freelancer/dashboard";
            case ADMIN -> "redirect:/admin/dashboard";
        };
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        return "redirect:/dashboard";
    }
}
