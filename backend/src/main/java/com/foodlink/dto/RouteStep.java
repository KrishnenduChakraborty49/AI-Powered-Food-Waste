package com.foodlink.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteStep {
    private int stepOrder;
    private String action; // "PICKUP" or "DELIVERY"
    private String locationName;
    private double lat;
    private double lng;
    private Long transactionId;
}
