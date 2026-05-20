package com.foodlink.security;

import com.foodlink.exception.RateLimitException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // Global limit per IP
    private final Map<String, Bucket> globalBuckets = new ConcurrentHashMap<>();
    
    // Specific limit for POST /api/listings
    private final Map<String, Bucket> createListingBuckets = new ConcurrentHashMap<>();

    private Bucket createNewGlobalBucket() {
        // 100 requests per minute
        Refill refill = Refill.greedy(100, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(100, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createNewListingBucket() {
        // 10 requests per hour for creating listings
        Refill refill = Refill.greedy(10, Duration.ofHours(1));
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ipAddress = request.getRemoteAddr();

        // Check global rate limit
        Bucket globalBucket = globalBuckets.computeIfAbsent(ipAddress, k -> createNewGlobalBucket());
        if (!globalBucket.tryConsume(1)) {
            throw new RateLimitException("Global API rate limit exceeded (100 requests / minute)");
        }

        // Check specific rate limit for creating listings
        if (request.getRequestURI().equals("/api/listings") && request.getMethod().equalsIgnoreCase("POST")) {
            Bucket listingBucket = createListingBuckets.computeIfAbsent(ipAddress, k -> createNewListingBucket());
            if (!listingBucket.tryConsume(1)) {
                throw new RateLimitException("Listing creation rate limit exceeded (10 requests / hour)");
            }
        }

        return true;
    }
}
