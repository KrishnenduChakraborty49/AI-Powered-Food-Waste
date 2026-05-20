package com.foodlink.dto;

import com.foodlink.model.enums.FoodType;
import com.foodlink.model.enums.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ListingRequest {
    @NotBlank
    private String foodName;

    @NotNull
    private FoodType foodType;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull
    private Unit unit;

    private String description;

    @NotBlank
    private String pickupAddress;

    @NotNull
    private BigDecimal lat;

    @NotNull
    private BigDecimal lng;

    @NotNull
    private LocalDateTime expiryTime;
}
