package com.foodlink.controller;

import com.foodlink.dto.ListingRequest;
import com.foodlink.dto.RecommendationResponse;
import com.foodlink.dto.RouteStep;
import com.foodlink.service.AiParsingService;
import com.foodlink.service.AiRecommendationService;
import com.foodlink.service.PathfindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiParsingService aiParsingService;

    @Autowired
    private AiRecommendationService aiRecommendationService;

    @Autowired
    private PathfindingService pathfindingService;

    @PostMapping("/parse-listing")
    public ResponseEntity<ListingRequest> parseListing(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        ListingRequest parsedRequest = aiParsingService.parseNaturalLanguage(text);
        return ResponseEntity.ok(parsedRequest);
    }

    @GetMapping("/recommend-ngo/{listingId}")
    public ResponseEntity<List<RecommendationResponse>> recommendNgos(@PathVariable Long listingId) {
        return ResponseEntity.ok(aiRecommendationService.recommendNgosForListing(listingId));
    }

    @GetMapping("/optimize-route")
    public ResponseEntity<List<RouteStep>> optimizeRoute(
            @RequestParam String email,
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(pathfindingService.optimizeRoute(email, lat, lng));
    }
}
