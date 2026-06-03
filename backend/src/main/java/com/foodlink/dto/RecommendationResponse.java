package com.foodlink.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationResponse {
    private Long ngoId;
    private String ngoName;
    private double distanceKm;
    private int capacity;
    private double priorityScore;
    private String rationale;
}
