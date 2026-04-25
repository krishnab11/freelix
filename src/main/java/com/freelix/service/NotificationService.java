package com.freelix.service;

import com.freelix.dto.NotificationDto;
import com.freelix.entity.Notification;
import com.freelix.entity.User;
import com.freelix.enums.NotificationType;
import com.freelix.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification create(User recipient, String message, NotificationType type, String link) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setMessage(message);
        n.setType(type);
        n.setLink(link);
        return notificationRepository.save(n);
    }

    public List<NotificationDto> getForUser(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());
    }

    public long countUnread(User user) {
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    public void markAllRead(User user) {
        notificationRepository.markAllReadForUser(user);
    }
}
