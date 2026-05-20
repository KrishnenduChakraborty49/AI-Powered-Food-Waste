package com.foodlink.repository;

import com.foodlink.model.FoodListing;
import com.foodlink.model.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodListingRepository extends JpaRepository<FoodListing, Long> {
    
    Page<FoodListing> findByDonorId(Long donorId, Pageable pageable);
    
    List<FoodListing> findByStatusAndExpiryTimeBefore(ListingStatus status, LocalDateTime now);

    @Query(value = "SELECT * FROM food_listings WHERE status = 'AVAILABLE' AND " +
            "ST_Distance_Sphere(POINT(lng, lat), POINT(:ngoLng, :ngoLat)) < :radiusMeters " +
            "ORDER BY priority_score DESC", nativeQuery = true)
    Page<FoodListing> findNearbyListings(@Param("ngoLat") double ngoLat, 
                                         @Param("ngoLng") double ngoLng, 
                                         @Param("radiusMeters") int radiusMeters,
                                         Pageable pageable);
}
