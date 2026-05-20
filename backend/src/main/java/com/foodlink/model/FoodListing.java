package com.foodlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foodlink.model.enums.FoodType;
import com.foodlink.model.enums.ListingStatus;
import com.foodlink.model.enums.Unit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id", nullable = false)
    @JsonIgnoreProperties({"password", "listings", "hibernateLazyInitializer", "handler"})
    private User donor;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_type", nullable = false, columnDefinition = "varchar(255)")
    private FoodType foodType;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private Unit unit;

    @Column(length = 1000)
    private String description;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(precision = 10, scale = 8, nullable = false)
    private BigDecimal lat;

    @Column(precision = 10, scale = 8, nullable = false)
    private BigDecimal lng;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Builder.Default
    private ListingStatus status = ListingStatus.AVAILABLE;

    @Column(name = "priority_score", precision = 5, scale = 2)
    private BigDecimal priorityScore;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
