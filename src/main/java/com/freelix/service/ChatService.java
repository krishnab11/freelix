package com.freelix.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.freelix.dto.ConversationSummaryDto;
import com.freelix.dto.MessageDto;
import com.freelix.entity.Message;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private Cloudinary cloudinary;

    public Message sendMessage(String content, User sender, User receiver, Project project) {
        Message msg = new Message();
        msg.setContent(content);
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setProject(project);
        return messageRepository.save(msg);
    }

    public Message sendMessageWithAttachment(String content, User sender, User receiver, Project project,
                                             MultipartFile file) throws IOException {
        Message msg = new Message();
        msg.setContent(content != null ? content : "");
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setProject(project);

        if (file != null && !file.isEmpty()) {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "freelix/chat", "resource_type", "auto")
            );
            msg.setAttachmentUrl(result.get("secure_url").toString());
            msg.setAttachmentName(file.getOriginalFilename());
        }
        return messageRepository.save(msg);
    }

    public MessageDto addReaction(Long messageId, String emoji) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        msg.setReaction(emoji);
        return MessageDto.from(messageRepository.save(msg));
    }

    public List<Message> getProjectMessages(Project project) {
        return messageRepository.findByProjectOrderBySentAtAsc(project);
    }

    public List<MessageDto> getNewMessages(Project project, Long lastId) {
        return messageRepository
                .findByProjectAndIdGreaterThanOrderBySentAtAsc(project, lastId)
                .stream()
                .map(MessageDto::from)
                .collect(Collectors.toList());
    }

    public long countUnread(User receiver) {
        return messageRepository.countByReceiverAndIsReadFalse(receiver);
    }

    public void markRead(Message message) {
        message.setIsRead(true);
        messageRepository.save(message);
    }

    public void markAllRead(User receiver) {
        messageRepository.findByReceiverAndIsReadFalse(receiver)
                .forEach(m -> { m.setIsRead(true); messageRepository.save(m); });
    }

    /**
     * Build a deduplicated list of conversations for the inbox.
     * Each conversation is identified by the unique (otherUser, project) pair.
     * Only the latest message per pair is returned.
     */
    public List<ConversationSummaryDto> getConversations(User user) {
        List<Message> allMessages = messageRepository.findAllByUser(user);

        // Key = "otherUserId_projectId"
        Map<String, ConversationSummaryDto> map = new LinkedHashMap<>();

        for (Message m : allMessages) {
            User other = m.getSender().getId().equals(user.getId()) ? m.getReceiver() : m.getSender();
            String key = other.getId() + "_" + (m.getProject() != null ? m.getProject().getId() : "0");

            if (!map.containsKey(key)) {
                ConversationSummaryDto dto = new ConversationSummaryDto();
                dto.setOtherUserId(other.getId());
                dto.setOtherUserName(other.getName());
                dto.setOtherUserAvatar(other.getProfileImageUrl());
                if (m.getProject() != null) {
                    dto.setProjectId(m.getProject().getId());
                    dto.setProjectTitle(m.getProject().getTitle());
                }
                String preview = m.getContent();
                if (preview == null || preview.isBlank()) preview = "\uD83D\uDCCE " + m.getAttachmentName();
                dto.setLastMessage(preview);
                dto.setLastMessagePreview(preview.length() > 60 ? preview.substring(0, 60) + "…" : preview);
                dto.setLastMessageAt(m.getSentAt());
                dto.setUnreadCount(0L);
                map.put(key, dto);
            }
        }

        // Count unread per conversation
        messageRepository.findByReceiverAndIsReadFalse(user).forEach(m -> {
            User other = m.getSender();
            String key = other.getId() + "_" + (m.getProject() != null ? m.getProject().getId() : "0");
            if (map.containsKey(key)) {
                map.get(key).setUnreadCount(map.get(key).getUnreadCount() + 1);
            }
        });

        return new ArrayList<>(map.values());
    }
}
