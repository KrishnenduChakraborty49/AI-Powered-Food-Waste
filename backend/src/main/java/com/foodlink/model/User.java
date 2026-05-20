package com.foodlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foodlink.model.enums.BadgeLevel;
import com.foodlink.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private Role role;

    @Column(precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(precision = 10, scale = 8)
    private BigDecimal lng;

    @Builder.Default
    @Column(name = "trust_score", precision = 3, scale = 2)
    private BigDecimal trustScore = new BigDecimal("5.0");

    @Builder.Default
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Builder.Default
    private Integer points = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_level", columnDefinition = "varchar(255)")
    @Builder.Default
    private BadgeLevel badgeLevel = BadgeLevel.BRONZE;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
