package com.foodlink.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AnalyticsSummary {
    private Long totalMealsSaved;
    private BigDecimal totalCo2ReducedKg;
    private Long activeListingsCount;
    private Long activeUsersCount;
}
