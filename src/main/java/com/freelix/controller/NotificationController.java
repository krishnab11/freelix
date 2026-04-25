package com.freelix.controller;

import com.freelix.dto.NotificationDto;
import com.freelix.security.CustomUserDetails;
import com.freelix.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired private NotificationService notificationService;

    @GetMapping
    public String notificationsPage(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        var user = ud.getUser();
        List<NotificationDto> notifications = notificationService.getForUser(user);
        notificationService.markAllRead(user); // auto-mark as read when page opened
        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);
        return "notifications/notifications";
    }

    @PostMapping("/mark-read")
    @ResponseBody
    public ResponseEntity<Map<String, String>> markAllRead(@AuthenticationPrincipal CustomUserDetails ud) {
        notificationService.markAllRead(ud.getUser());
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal CustomUserDetails ud) {
        long count = notificationService.countUnread(ud.getUser());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
