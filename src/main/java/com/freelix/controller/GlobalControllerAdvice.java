package com.freelix.controller;

import com.freelix.security.CustomUserDetails;
import com.freelix.service.ChatService;
import com.freelix.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ChatService chatService;

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("unreadNotifications")
    public long unreadNotifications(@AuthenticationPrincipal CustomUserDetails ud) {
        if (ud == null) return 0;
        try {
            return notificationService.countUnread(ud.getUser());
        } catch (Exception e) {
            return 0;
        }
    }

    @ModelAttribute("unreadMessages")
    public long unreadMessages(@AuthenticationPrincipal CustomUserDetails ud) {
        if (ud == null) return 0;
        try {
            return chatService.countUnread(ud.getUser());
        } catch (Exception e) {
            return 0;
        }
    }
}
