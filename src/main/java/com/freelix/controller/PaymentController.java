package com.freelix.controller;

import com.freelix.entity.Payment;
import com.freelix.entity.Project;
import com.freelix.security.CustomUserDetails;
import com.freelix.service.PaymentService;
import com.freelix.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired private PaymentService paymentService;
    @Autowired private ProjectService projectService;

    @PostMapping("/pay/{projectId}")
    public String processPayment(@PathVariable Long projectId,
                                 @AuthenticationPrincipal CustomUserDetails ud,
                                 RedirectAttributes ra) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getClient().getId().equals(ud.getUser().getId())) {
            ra.addFlashAttribute("error", "Unauthorized action.");
            return "redirect:/client/dashboard";
        }
        try {
            Payment payment = paymentService.processPayment(project, ud.getUser());
            ra.addFlashAttribute("success", "Payment processed! Invoice: " + payment.getInvoiceNumber());
            return "redirect:/payment/invoice/" + payment.getId();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/client/projects/" + projectId + "/applicants";
        }
    }

    @GetMapping("/invoice/{paymentId}")
    public String invoicePage(@PathVariable Long paymentId,
                              @AuthenticationPrincipal CustomUserDetails ud,
                              Model model) {
        Payment payment = paymentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        model.addAttribute("user", ud.getUser());
        model.addAttribute("payment", payment);
        model.addAttribute("project", payment.getProject());
        model.addAttribute("client", payment.getClient());
        model.addAttribute("freelancer", payment.getFreelancer());
        return "payment/invoice";
    }
}
