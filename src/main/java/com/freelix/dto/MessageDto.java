package com.freelix.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class MessageDto {

    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String attachmentUrl;
    private String attachmentName;
    private String reaction;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime sentAt;

    public MessageDto() {}

    public static MessageDto from(com.freelix.entity.Message msg) {
        MessageDto dto = new MessageDto();
        dto.setId(msg.getId());
        dto.setContent(msg.getContent());
        dto.setSenderId(msg.getSender().getId());
        dto.setSenderName(msg.getSender().getName());
        dto.setReceiverId(msg.getReceiver().getId());
        dto.setSentAt(msg.getSentAt());
        dto.setAttachmentUrl(msg.getAttachmentUrl());
        dto.setAttachmentName(msg.getAttachmentName());
        dto.setReaction(msg.getReaction());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }

    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
