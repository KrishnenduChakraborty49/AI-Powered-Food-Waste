package com.foodlink.controller;

import com.foodlink.dto.TransactionResponse;
import com.foodlink.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/volunteer")
@PreAuthorize("hasRole('VOLUNTEER')")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping("/available")
    public ResponseEntity<Page<TransactionResponse>> getAvailableDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(volunteerService.getAvailableDeliveries(PageRequest.of(page, size)));
    }

    @PostMapping("/{transactionId}/accept")
    public ResponseEntity<TransactionResponse> acceptDelivery(
            @PathVariable Long transactionId,
            Authentication authentication) {
        return ResponseEntity.ok(volunteerService.acceptDelivery(transactionId, authentication.getName()));
    }

    @PostMapping("/{transactionId}/delivered")
    public ResponseEntity<TransactionResponse> markDelivered(
            @PathVariable Long transactionId,
            Authentication authentication) {
        return ResponseEntity.ok(volunteerService.markDelivered(transactionId, authentication.getName()));
    }
}
