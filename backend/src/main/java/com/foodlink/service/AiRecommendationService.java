package com.foodlink.service;

import com.foodlink.dto.RecommendationResponse;
import com.foodlink.model.FoodListing;
import com.foodlink.model.User;
import com.foodlink.model.enums.Role;
import com.foodlink.repository.FoodListingRepository;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AiRecommendationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodListingRepository listingRepository;

    public List<RecommendationResponse> recommendNgosForListing(Long listingId) {
        FoodListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        List<User> ngos = userRepository.findAll(); // In a real app, query by Role and Radius
        List<RecommendationResponse> recommendations = new ArrayList<>();

        long hoursRemaining = ChronoUnit.HOURS.between(LocalDateTime.now(), listing.getExpiryTime());
        if (hoursRemaining < 0) hoursRemaining = 0;
        
        // Base urgency score (0-100), higher is more urgent
        double urgencyFactor = 1.0 - (Math.min(hoursRemaining, 12.0) / 12.0);
        double urgencyScore = urgencyFactor * 100;

        for (User ngo : ngos) {
            if (ngo.getRole() != Role.NGO || ngo.getLat() == null) continue;

            // Calculate distance roughly using simple Pythagorean for fast demo (should use ST_Distance in prod)
            double distanceKm = calculateDistance(
                    listing.getLat().doubleValue(), listing.getLng().doubleValue(),
                    ngo.getLat().doubleValue(), ngo.getLng().doubleValue()
            );

            // Distance score (0-100), closer is higher
            double distanceScore = Math.max(0, 100 - (distanceKm * 5)); 

            // Capacity score (0-100), higher capacity = higher score, capped at 100
            int capacity = ngo.getNgoCapacity() != null ? ngo.getNgoCapacity() : 50;
            double capacityScore = Math.min(100, (capacity / 100.0) * 100);

            // Dynamic weights based on Urgency!
            // If urgent (high urgencyScore), distance matters MUCH more.
            // If not urgent, capacity matters more (can take bulk).
            double wDistance, wCapacity;
            if (urgencyScore > 80) {
                wDistance = 0.8;
                wCapacity = 0.2;
            } else {
                wDistance = 0.4;
                wCapacity = 0.6;
            }

            double finalScore = (distanceScore * wDistance) + (capacityScore * wCapacity);

            String rationale = String.format("Matches %.0f%%: %s", 
                finalScore,
                (urgencyScore > 80 ? "Critical urgency favored proximity." : "High capacity favored for bulk storage.")
            );

            recommendations.add(RecommendationResponse.builder()
                    .ngoId(ngo.getId())
                    .ngoName(ngo.getName())
                    .distanceKm(Math.round(distanceKm * 10.0) / 10.0)
                    .capacity(capacity)
                    .priorityScore(Math.round(finalScore))
                    .rationale(rationale)
                    .build());
        }

        recommendations.sort(Comparator.comparing(RecommendationResponse::getPriorityScore).reversed());
        return recommendations.subList(0, Math.min(3, recommendations.size())); // Top 3
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
