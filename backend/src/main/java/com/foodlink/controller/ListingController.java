package com.foodlink.controller;

import com.foodlink.dto.ListingRequest;
import com.foodlink.dto.ListingResponse;
import com.foodlink.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @PostMapping
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ListingResponse> createListing(@Valid @RequestBody ListingRequest request, Authentication authentication) {
        return ResponseEntity.ok(listingService.createListing(request, authentication.getName()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<Page<ListingResponse>> getMyListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(listingService.getMyListings(authentication.getName(), PageRequest.of(page, size)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<Void> deleteListing(@PathVariable Long id, Authentication authentication) {
        listingService.deleteListing(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nearby")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<Page<ListingResponse>> getNearbyListings(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5000") int radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listingService.getNearbyListings(lat, lng, radius, PageRequest.of(page, size)));
    }
}
