package com.foodlink.service;

import com.foodlink.model.FoodListing;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class PriorityScoreService {

    public BigDecimal computeScore(FoodListing listing, double distanceKm) {
        long hoursRemaining = ChronoUnit.HOURS.between(LocalDateTime.now(), listing.getExpiryTime());
        if (hoursRemaining < 0) hoursRemaining = 0;

        // urgencyScore = (1 - hoursRemaining / 12.0) × 100 × 0.45 [max 45]
        double urgencyFactor = 1.0 - (Math.min(hoursRemaining, 12.0) / 12.0);
        double urgencyScore = urgencyFactor * 100 * 0.45;

        // proximityScore = (1 - distanceKm / 20.0) × 100 × 0.30 [max 30]
        double proximityFactor = 1.0 - (Math.min(distanceKm, 20.0) / 20.0);
        double proximityScore = proximityFactor * 100 * 0.30;

        // volumeScore = (quantity / 200.0) × 100 × 0.15 [max 15]
        double volumeFactor = Math.min(listing.getQuantity(), 200.0) / 200.0;
        double volumeScore = volumeFactor * 100 * 0.15;

        // trustScore = (donor.trustScore / 5.0) × 100 × 0.10 [max 10]
        double trustFactor = listing.getDonor().getTrustScore().doubleValue() / 5.0;
        double trustScore = trustFactor * 100 * 0.10;

        double finalScore = urgencyScore + proximityScore + volumeScore + trustScore;
        finalScore = Math.max(0, Math.min(100, finalScore)); // clamp between 0 and 100

        return BigDecimal.valueOf(finalScore).setScale(2, RoundingMode.HALF_UP);
    }
}
