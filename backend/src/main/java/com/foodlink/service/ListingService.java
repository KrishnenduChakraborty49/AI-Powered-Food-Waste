package com.foodlink.service;

import com.foodlink.dto.ListingRequest;
import com.foodlink.dto.ListingResponse;
import com.foodlink.model.FoodListing;
import com.foodlink.model.User;
import com.foodlink.model.enums.ListingStatus;
import com.foodlink.repository.FoodListingRepository;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ListingService {

    @Autowired
    private FoodListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PriorityScoreService priorityScoreService;

    @Transactional
    public ListingResponse createListing(ListingRequest request, String donorEmail) {
        User donor = userRepository.findByEmail(donorEmail)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        FoodListing listing = FoodListing.builder()
                .donor(donor)
                .foodName(request.getFoodName())
                .foodType(request.getFoodType())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .description(request.getDescription())
                .pickupAddress(request.getPickupAddress())
                .lat(request.getLat())
                .lng(request.getLng())
                .expiryTime(request.getExpiryTime())
                .status(ListingStatus.AVAILABLE)
                .build();

        // Initial score with 0 distance (just based on urgency, volume, trust)
        listing.setPriorityScore(priorityScoreService.computeScore(listing, 0.0));
        
        FoodListing savedListing = listingRepository.save(listing);
        return mapToResponse(savedListing);
    }

    public Page<ListingResponse> getMyListings(String email, Pageable pageable) {
        User donor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        return listingRepository.findByDonorId(donor.getId(), pageable).map(this::mapToResponse);
    }

    @Transactional
    public void deleteListing(Long id, String email) {
        FoodListing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        if (!listing.getDonor().getEmail().equals(email)) {
            throw new RuntimeException("Not authorized to delete this listing");
        }
        
        listing.setStatus(ListingStatus.EXPIRED);
        listingRepository.save(listing);
    }

    public Page<ListingResponse> getNearbyListings(double ngoLat, double ngoLng, int radiusMeters, Pageable pageable) {
        Page<FoodListing> listings = listingRepository.findNearbyListings(ngoLat, ngoLng, radiusMeters, pageable);
        
        // Recompute priority score dynamically based on actual NGO distance
        return listings.map(listing -> {
            double distanceKm = calculateHaversineDistance(ngoLat, ngoLng, listing.getLat().doubleValue(), listing.getLng().doubleValue());
            BigDecimal dynamicScore = priorityScoreService.computeScore(listing, distanceKm);
            listing.setPriorityScore(dynamicScore);
            return mapToResponse(listing);
        });
    }

    private ListingResponse mapToResponse(FoodListing listing) {
        return ListingResponse.builder()
                .id(listing.getId())
                .donorName(listing.getDonor().getName())
                .donorTrustScore(listing.getDonor().getTrustScore())
                .foodName(listing.getFoodName())
                .foodType(listing.getFoodType())
                .quantity(listing.getQuantity())
                .unit(listing.getUnit())
                .description(listing.getDescription())
                .pickupAddress(listing.getPickupAddress())
                .lat(listing.getLat())
                .lng(listing.getLng())
                .expiryTime(listing.getExpiryTime())
                .status(listing.getStatus())
                .priorityScore(listing.getPriorityScore())
                .createdAt(listing.getCreatedAt())
                .build();
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // distance in km
    }
}
