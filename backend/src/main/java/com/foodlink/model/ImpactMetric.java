package com.foodlink.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "impact_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    private String city;

    @Column(name = "food_kg", precision = 8, scale = 2, nullable = false)
    private BigDecimal foodKg;

    @Column(name = "meals_count", nullable = false)
    private Integer mealsCount;

    @Column(name = "co2_saved_kg", precision = 8, scale = 2, nullable = false)
    private BigDecimal co2SavedKg;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
}
