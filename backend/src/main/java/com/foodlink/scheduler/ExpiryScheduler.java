package com.foodlink.scheduler;

import com.foodlink.model.FoodListing;
import com.foodlink.model.Notification;
import com.foodlink.model.enums.ListingStatus;
import com.foodlink.model.enums.NotificationChannel;
import com.foodlink.model.enums.NotificationType;
import com.foodlink.repository.FoodListingRepository;
import com.foodlink.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.foodlink.service.EmailService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpiryScheduler {

    @Autowired
    private FoodListingRepository listingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 900000) // every 15 minutes
    @Transactional
    public void markExpiredListings() {
        LocalDateTime now = LocalDateTime.now();
        List<FoodListing> expiredListings = listingRepository.findByStatusAndExpiryTimeBefore(ListingStatus.AVAILABLE, now);

        for (FoodListing listing : expiredListings) {
            listing.setStatus(ListingStatus.EXPIRED);
            
            // Send EXPIRY_WARNING notification to donor
            Notification notification = Notification.builder()
                    .user(listing.getDonor())
                    .listing(listing)
                    .type(NotificationType.EXPIRY_WARNING)
                    .channel(NotificationChannel.IN_APP)
                    .message("Your listing '" + listing.getFoodName() + "' has expired.")
                    .isRead(false)
                    .build();
            
            notificationRepository.save(notification);
            
            // Real-world email notification
            String subject = "Urgent: Your Food Listing Expired";
            String body = "Dear " + listing.getDonor().getName() + ",\n\n" +
                          "Your listing for '" + listing.getFoodName() + "' has expired as it was not claimed in time.\n" +
                          "Please dispose of it safely.\n\n" +
                          "Thank you for using FoodLink.";
            emailService.sendEmail(listing.getDonor().getEmail(), subject, body);
        }

        if (!expiredListings.isEmpty()) {
            listingRepository.saveAll(expiredListings);
            System.out.println("Marked " + expiredListings.size() + " listings as EXPIRED.");
        }
    }
}
