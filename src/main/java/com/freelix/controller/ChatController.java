package com.freelix.controller;

import com.freelix.dto.ConversationSummaryDto;
import com.freelix.dto.MessageDto;
import com.freelix.entity.Message;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.security.CustomUserDetails;
import com.freelix.service.ChatService;
import com.freelix.service.ProjectService;
import com.freelix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired private ChatService chatService;
    @Autowired private ProjectService projectService;
    @Autowired private UserService userService;

    // ── Messages inbox ──────────────────────────────────────────────────────
    @GetMapping("/messages")
    public String messagesInbox(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        User user = ud.getUser();
        List<ConversationSummaryDto> conversations = chatService.getConversations(user);
        model.addAttribute("user", user);
        model.addAttribute("conversations", conversations);
        return "messages/inbox";
    }

    // ── Chat page ────────────────────────────────────────────────────────────
    @GetMapping("/chat/{projectId}")
    public String chatPage(@PathVariable Long projectId,
                           @AuthenticationPrincipal CustomUserDetails ud,
                           Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User me = ud.getUser();
        List<Message> messages = chatService.getProjectMessages(project);

        // Mark incoming messages as read
        chatService.markAllRead(me);

        User other;
        if (me.getId().equals(project.getClient().getId())) {
            other = project.getSelectedFreelancer();
        } else {
            other = project.getClient();
        }

        model.addAttribute("user", me);
        model.addAttribute("project", project);
        model.addAttribute("messages", messages);
        model.addAttribute("other", other);
        long lastId = messages.isEmpty() ? 0L : messages.get(messages.size() - 1).getId();
        model.addAttribute("lastId", lastId);
        return "chat/chat";
    }

    // ── Send message (text + optional file) ──────────────────────────────────
    @PostMapping("/chat/send")
    @ResponseBody
    public ResponseEntity<MessageDto> sendMessage(
            @RequestParam Long projectId,
            @RequestParam Long receiverId,
            @RequestParam(required = false, defaultValue = "") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails ud) {

        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User receiver = userService.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        try {
            Message msg = chatService.sendMessageWithAttachment(
                    content.isBlank() ? null : content.trim(),
                    ud.getUser(), receiver, project, file);
            return ResponseEntity.ok(MessageDto.from(msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ── Poll for new messages ─────────────────────────────────────────────────
    @GetMapping("/chat/poll")
    @ResponseBody
    public ResponseEntity<List<MessageDto>> pollMessages(
            @RequestParam Long projectId,
            @RequestParam(defaultValue = "0") Long lastId,
            @AuthenticationPrincipal CustomUserDetails ud) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<MessageDto> newMessages = chatService.getNewMessages(project, lastId);
        return ResponseEntity.ok(newMessages);
    }

    // ── Add / toggle reaction ─────────────────────────────────────────────────
    @PostMapping("/chat/react")
    @ResponseBody
    public ResponseEntity<MessageDto> reactToMessage(
            @RequestParam Long messageId,
            @RequestParam String emoji,
            @AuthenticationPrincipal CustomUserDetails ud) {
        MessageDto dto = chatService.addReaction(messageId, emoji);
        return ResponseEntity.ok(dto);
    }
}
