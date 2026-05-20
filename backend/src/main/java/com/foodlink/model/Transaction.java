package com.foodlink.model;

import com.foodlink.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private FoodListing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    private User ngo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id")
    private User volunteer;

    @CreationTimestamp
    @Column(name = "claimed_at", updatable = false)
    private LocalDateTime claimedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, columnDefinition = "varchar(255)")
    @Builder.Default
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "otp_code", length = 6)
    private String otpCode;

    @Column(name = "otp_verified")
    @Builder.Default
    private Boolean otpVerified = false;

    @Column(name = "donor_rating")
    private Integer donorRating;

    @Column(name = "ngo_rating")
    private Integer ngoRating;

    @Column(length = 500)
    private String notes;
}
