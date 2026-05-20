package com.foodlink.service;

import com.foodlink.model.FoodListing;
import com.foodlink.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PriorityScoreServiceTest {

    @InjectMocks
    private PriorityScoreService priorityScoreService;

    private FoodListing listing;
    private User donor;

    @BeforeEach
    void setUp() {
        donor = new User();
        donor.setTrustScore(new BigDecimal("5.0"));

        listing = new FoodListing();
        listing.setDonor(donor);
        listing.setQuantity(100);
    }

    @Test
    void testComputeScore_HighUrgency_HighScore() {
        // Expiring in 1 hour (very urgent)
        listing.setExpiryTime(LocalDateTime.now().plusHours(1));
        
        // Very close (1km)
        double distanceKm = 1.0;

        BigDecimal score = priorityScoreService.computeScore(listing, distanceKm);
        
        // Score should be high (closer to 100)
        assertTrue(score.doubleValue() > 75.0);
    }

    @Test
    void testComputeScore_LowUrgency_LowScore() {
        // Expiring in 24 hours (not urgent, urgency factor will be 0 because > 12)
        listing.setExpiryTime(LocalDateTime.now().plusHours(24));
        
        // Very far (30km, proximity factor will be 0 because > 20)
        double distanceKm = 30.0;
        
        // Low quantity (10)
        listing.setQuantity(10);
        
        // Low trust score (1.0)
        donor.setTrustScore(new BigDecimal("1.0"));

        BigDecimal score = priorityScoreService.computeScore(listing, distanceKm);
        
        // Score should be very low (only volume and trust contribute a tiny amount)
        assertTrue(score.doubleValue() < 10.0);
    }
}
