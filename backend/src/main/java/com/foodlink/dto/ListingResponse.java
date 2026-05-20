package com.foodlink.dto;

import com.foodlink.model.enums.FoodType;
import com.foodlink.model.enums.ListingStatus;
import com.foodlink.model.enums.Unit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ListingResponse {
    private Long id;
    private String donorName;
    private BigDecimal donorTrustScore;
    private String foodName;
    private FoodType foodType;
    private Integer quantity;
    private Unit unit;
    private String description;
    private String pickupAddress;
    private BigDecimal lat;
    private BigDecimal lng;
    private LocalDateTime expiryTime;
    private ListingStatus status;
    private BigDecimal priorityScore;
    private LocalDateTime createdAt;
}
