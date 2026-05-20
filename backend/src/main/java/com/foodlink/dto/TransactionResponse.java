package com.foodlink.dto;

import com.foodlink.model.enums.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private Long listingId;
    private String foodName;
    private String donorName;
    private String ngoName;
    private String volunteerName;
    private LocalDateTime claimedAt;
    private LocalDateTime deliveredAt;
    private DeliveryStatus deliveryStatus;
    private Boolean otpVerified;
    private Integer donorRating;
    private Integer ngoRating;
}
