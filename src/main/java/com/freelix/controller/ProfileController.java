package com.freelix.controller;

import com.freelix.entity.User;
import com.freelix.enums.FileType;
import com.freelix.security.CustomUserDetails;
import com.freelix.service.FileService;
import com.freelix.service.ProjectService;
import com.freelix.service.ReviewService;
import com.freelix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired private UserService userService;
    @Autowired private FileService fileService;
    @Autowired private ReviewService reviewService;
    @Autowired private ProjectService projectService;

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        User user = ud.getUser();
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviewService.findByFreelancer(user));
        model.addAttribute("files", fileService.getFilesByUploader(user));
        return "profile/profile";
    }

    @GetMapping("/view/{userId}")
    public String publicProfile(@PathVariable Long userId,
                                @AuthenticationPrincipal CustomUserDetails ud,
                                Model model) {
        User target = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", ud.getUser());
        model.addAttribute("target", target);
        model.addAttribute("reviews", reviewService.findByFreelancer(target));
        model.addAttribute("files", fileService.getFilesByUploader(target));
        model.addAttribute("completedProjects", projectService.findByFreelancer(target));
        return "profile/public-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails ud,
                                @RequestParam String name,
                                @RequestParam(required = false) String bio,
                                @RequestParam(required = false) String skills,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String phone,
                                RedirectAttributes ra) {
        userService.updateProfile(ud.getUser(), name, bio, skills, location, phone);
        ra.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/upload/photo")
    public String uploadPhoto(@AuthenticationPrincipal CustomUserDetails ud,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes ra) {
        try {
            var record = fileService.uploadFile(file, FileType.PROFILE, ud.getUser(), null);
            userService.updateProfileImage(ud.getUser(), record.getCloudinaryUrl());
            ra.addFlashAttribute("success", "Profile photo updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/upload/resume")
    public String uploadResume(@AuthenticationPrincipal CustomUserDetails ud,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes ra) {
        try {
            var record = fileService.uploadFile(file, FileType.RESUME, ud.getUser(), null);
            userService.updateResume(ud.getUser(), record.getCloudinaryUrl());
            ra.addFlashAttribute("success", "Resume uploaded!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/upload/certificate")
    public String uploadCertificate(@AuthenticationPrincipal CustomUserDetails ud,
                                    @RequestParam("file") MultipartFile file,
                                    RedirectAttributes ra) {
        try {
            fileService.uploadFile(file, FileType.CERTIFICATE, ud.getUser(), null);
            ra.addFlashAttribute("success", "Certificate uploaded!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
