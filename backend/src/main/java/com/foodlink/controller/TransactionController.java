package com.foodlink.controller;

import com.foodlink.dto.TransactionResponse;
import com.foodlink.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/claim/{listingId}")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<TransactionResponse> claimListing(@PathVariable Long listingId, Authentication authentication) {
        return ResponseEntity.ok(transactionService.claimListing(listingId, authentication.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<TransactionResponse>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.getMyTransactions(authentication.getName(), PageRequest.of(page, size)));
    }

    @PostMapping("/{id}/confirm-otp")
    @PreAuthorize("hasRole('VOLUNTEER') or hasRole('NGO')")
    public ResponseEntity<TransactionResponse> confirmOtp(
            @PathVariable Long id,
            @RequestParam String otpCode) {
        return ResponseEntity.ok(transactionService.confirmOtp(id, otpCode));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<TransactionResponse> rateTransaction(
            @PathVariable Long id,
            @RequestParam Integer rating,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.rateTransaction(id, rating, authentication.getName()));
    }
}
