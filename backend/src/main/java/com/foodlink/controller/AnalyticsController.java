package com.foodlink.controller;

import com.foodlink.dto.AnalyticsSummary;
import com.foodlink.dto.TransactionResponse;
import com.foodlink.model.FoodListing;
import com.foodlink.model.User;
import com.foodlink.repository.FoodListingRepository;
import com.foodlink.repository.TransactionRepository;
import com.foodlink.repository.UserRepository;
import com.foodlink.service.AnalyticsService;
import com.foodlink.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FoodListingRepository listingRepository;

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummary> getSummary() {
        return ResponseEntity.ok(analyticsService.getSummary());
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        return ResponseEntity.ok(analyticsService.getLeaderboard());
    }

    // ---- Admin endpoints ----

    @GetMapping("/admin/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @GetMapping("/admin/transactions")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            transactionRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "claimedAt")))
                .map(tx -> TransactionResponse.builder()
                    .id(tx.getId())
                    .listingId(tx.getListing().getId())
                    .foodName(tx.getListing().getFoodName())
                    .donorName(tx.getListing().getDonor().getName())
                    .ngoName(tx.getNgo().getName())
                    .volunteerName(tx.getVolunteer() != null ? tx.getVolunteer().getName() : null)
                    .claimedAt(tx.getClaimedAt())
                    .deliveredAt(tx.getDeliveredAt())
                    .deliveryStatus(tx.getDeliveryStatus())
                    .otpVerified(tx.getOtpVerified())
                    .donorRating(tx.getDonorRating())
                    .ngoRating(tx.getNgoRating())
                    .build())
        );
    }

    @GetMapping("/admin/listings")
    public ResponseEntity<Page<FoodListing>> getAllListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listingRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

