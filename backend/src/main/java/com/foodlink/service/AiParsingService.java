package com.foodlink.service;

import com.foodlink.dto.ListingRequest;
import com.foodlink.model.enums.FoodType;
import com.foodlink.model.enums.Unit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AiParsingService {

    /**
     * In a real enterprise app, this would make an HTTP call to OpenAI API
     * (e.g. using RestTemplate or Spring AI). For now, it acts as a heuristic parser.
     */
    public ListingRequest parseNaturalLanguage(String text) {
        ListingRequest request = new ListingRequest();
        String lowerText = text.toLowerCase();
        
        // 1. Food Type detection
        if (lowerText.contains("veg") && !lowerText.contains("non-veg")) {
            request.setFoodType(FoodType.VEG);
        } else if (lowerText.contains("meat") || lowerText.contains("chicken") || lowerText.contains("non-veg")) {
            request.setFoodType(FoodType.NON_VEG);
        } else if (lowerText.contains("bread") || lowerText.contains("rice") || lowerText.contains("grain")) {
            request.setFoodType(FoodType.GRAIN);
        } else {
            request.setFoodType(FoodType.VEG); // default
        }

        // 2. Quantity & Unit detection
        int quantity = 10; // default
        Unit unit = Unit.PORTIONS;
        
        // Simple regex-like heuristic to find numbers
        String[] words = lowerText.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].matches("\\d+")) {
                quantity = Integer.parseInt(words[i]);
                if (i + 1 < words.length) {
                    if (words[i+1].contains("kg")) unit = Unit.KG;
                    else if (words[i+1].contains("liter") || words[i+1].contains("litre")) unit = Unit.LITRES;
                }
                break;
            }
        }
        
        request.setQuantity(quantity);
        request.setUnit(unit);
        
        // 3. Expiry detection
        int addHours = 24; // default
        if (lowerText.contains("tonight")) addHours = 6;
        else if (lowerText.contains("tomorrow")) addHours = 24;
        else if (lowerText.contains("hours")) {
            for (int i = 0; i < words.length; i++) {
                if (words[i].contains("hour") && i > 0 && words[i-1].matches("\\d+")) {
                    addHours = Integer.parseInt(words[i-1]);
                }
            }
        }
        request.setExpiryTime(LocalDateTime.now().plusHours(addHours));
        
        // 4. Name extraction
        request.setFoodName(text);
        
        // Defaults for lat/lng (usually fetched from donor's profile, but we need defaults for the DTO)
        request.setLat(new BigDecimal("0.0"));
        request.setLng(new BigDecimal("0.0"));
        request.setPickupAddress("Current Location");
        
        return request;
    }
}
