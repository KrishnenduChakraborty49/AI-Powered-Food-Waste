package com.foodlink.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private String type;
    private String message;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private Long listingId;
    private String foodName;
}
