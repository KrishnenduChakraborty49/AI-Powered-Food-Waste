package com.foodlink.service;

import com.foodlink.dto.AnalyticsSummary;
import com.foodlink.model.ImpactMetric;
import com.foodlink.model.User;
import com.foodlink.model.enums.ListingStatus;
import com.foodlink.repository.FoodListingRepository;
import com.foodlink.repository.ImpactMetricRepository;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private ImpactMetricRepository impactMetricRepository;

    @Autowired
    private FoodListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    public AnalyticsSummary getSummary() {
        List<ImpactMetric> allMetrics = impactMetricRepository.findAll();
        
        long totalMeals = allMetrics.stream()
                .mapToLong(ImpactMetric::getMealsCount)
                .sum();
                
        BigDecimal totalCo2 = allMetrics.stream()
                .map(ImpactMetric::getCo2SavedKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        long activeListings = listingRepository.findAll().stream()
                .filter(l -> l.getStatus() == ListingStatus.AVAILABLE)
                .count();
                
        long activeUsers = userRepository.count();

        // If DB is empty, provide some mock data for demo purposes
        if (totalMeals == 0) totalMeals = 12450;
        if (totalCo2.equals(BigDecimal.ZERO)) totalCo2 = new BigDecimal("4350.5");

        return AnalyticsSummary.builder()
                .totalMealsSaved(totalMeals)
                .totalCo2ReducedKg(totalCo2)
                .activeListingsCount(activeListings)
                .activeUsersCount(activeUsers)
                .build();
    }
    
    public List<User> getLeaderboard() {
        // Find top 10 users by points
        return userRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "points"))).getContent();
    }
}
