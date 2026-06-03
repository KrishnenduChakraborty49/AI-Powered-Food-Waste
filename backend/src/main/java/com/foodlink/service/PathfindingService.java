package com.foodlink.service;

import com.foodlink.dto.RouteStep;
import com.foodlink.model.Transaction;
import com.foodlink.model.User;
import com.foodlink.model.enums.DeliveryStatus;
import com.foodlink.repository.TransactionRepository;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PathfindingService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RouteStep> optimizeRoute(String volunteerEmail, double currentLat, double currentLng) {
        User volunteer = userRepository.findByEmail(volunteerEmail)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        // Get all transactions assigned to this volunteer that are IN_TRANSIT (need delivery)
        // or PENDING (need pickup).
        // Since our model currently links volunteer upon "ACCEPT", let's assume they have multiple active.
        List<Transaction> activeDeliveries = transactionRepository.findByVolunteerIdAndDeliveryStatusIn(
                volunteer.getId(),
                List.of(DeliveryStatus.PENDING, DeliveryStatus.IN_TRANSIT)
        );

        List<RouteStep> unvisited = new ArrayList<>();
        
        for (Transaction t : activeDeliveries) {
            if (t.getDeliveryStatus() == DeliveryStatus.PENDING) {
                unvisited.add(RouteStep.builder()
                        .action("PICKUP")
                        .locationName(t.getListing().getPickupAddress())
                        .lat(t.getListing().getLat().doubleValue())
                        .lng(t.getListing().getLng().doubleValue())
                        .transactionId(t.getId())
                        .build());
            } else if (t.getDeliveryStatus() == DeliveryStatus.IN_TRANSIT) {
                unvisited.add(RouteStep.builder()
                        .action("DELIVERY")
                        .locationName(t.getNgo().getName() + " Location") // Assuming NGO has address or using LatLng
                        .lat(t.getNgo().getLat().doubleValue())
                        .lng(t.getNgo().getLng().doubleValue())
                        .transactionId(t.getId())
                        .build());
            }
        }

        // Greedy Nearest-Neighbor Algorithm
        List<RouteStep> optimizedRoute = new ArrayList<>();
        double currLat = currentLat;
        double currLng = currentLng;
        int stepCount = 1;

        while (!unvisited.isEmpty()) {
            RouteStep nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (RouteStep step : unvisited) {
                double dist = calculateDistance(currLat, currLng, step.getLat(), step.getLng());
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = step;
                }
            }

            if (nearest != null) {
                nearest.setStepOrder(stepCount++);
                optimizedRoute.add(nearest);
                unvisited.remove(nearest);
                currLat = nearest.getLat();
                currLng = nearest.getLng();
            }
        }

        return optimizedRoute;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
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
