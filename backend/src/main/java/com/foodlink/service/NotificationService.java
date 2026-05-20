package com.foodlink.service;

import com.foodlink.dto.NotificationDTO;
import com.foodlink.model.FoodListing;
import com.foodlink.model.Notification;
import com.foodlink.model.User;
import com.foodlink.model.enums.NotificationChannel;
import com.foodlink.model.enums.NotificationType;
import com.foodlink.repository.NotificationRepository;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send an in-app notification to a specific user.
     */
    @Transactional
    public void sendNotification(User recipient, FoodListing listing, NotificationType type, String message) {
        Notification notification = Notification.builder()
                .user(recipient)
                .listing(listing)
                .type(type)
                .channel(NotificationChannel.IN_APP)
                .message(message)
                .isRead(false)
                .build();
        notification = notificationRepository.save(notification);

        // Convert and send via WebSocket
        NotificationDTO dto = mapToDTO(notification);
        messagingTemplate.convertAndSendToUser(
            recipient.getEmail(), // User entity has getEmail(), not getUsername()
            "/queue/notifications", 
            dto
        );
    }

    /**
     * Get all notifications for the currently logged-in user.=
     */
    public List<NotificationDTO> getMyNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserIdOrderBySentAtDesc(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get the count of unread notifications.
     */
    public int getUnreadCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    /**
     * Mark a notification as read.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    /**
     * Mark all notifications for a user as read.
     */
    @Transactional
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> unread = notificationRepository.findByUserIdOrderBySentAtDesc(user.getId())
                .stream()
                .filter(n -> !n.getIsRead())
                .collect(Collectors.toList());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationDTO mapToDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .type(n.getType().name())
                .message(n.getMessage())
                .isRead(n.getIsRead())
                .sentAt(n.getSentAt())
                .listingId(n.getListing() != null ? n.getListing().getId() : null)
                .foodName(n.getListing() != null ? n.getListing().getFoodName() : null)
                .build();
    }
}
