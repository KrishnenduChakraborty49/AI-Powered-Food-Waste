package com.foodlink.model;

import com.foodlink.model.enums.VolunteerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "volunteer_deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private User volunteer;

    @Column(name = "pickup_lat", precision = 10, scale = 8, nullable = false)
    private BigDecimal pickupLat;

    @Column(name = "pickup_lng", precision = 10, scale = 8, nullable = false)
    private BigDecimal pickupLng;

    @Column(name = "drop_lat", precision = 10, scale = 8, nullable = false)
    private BigDecimal dropLat;

    @Column(name = "drop_lng", precision = 10, scale = 8, nullable = false)
    private BigDecimal dropLng;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "points_earned")
    private Integer pointsEarned;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Builder.Default
    private VolunteerStatus status = VolunteerStatus.ASSIGNED;
}
