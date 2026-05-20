package com.foodlink.service;

import com.foodlink.dto.TransactionResponse;
import com.foodlink.model.Transaction;
import com.foodlink.model.User;
import com.foodlink.model.enums.DeliveryStatus;
import com.foodlink.model.enums.NotificationType;
import com.foodlink.repository.TransactionRepository;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VolunteerService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private NotificationService notificationService;

    // Conceptual method - in a real app this would use spatial querying on transactions table
    public Page<TransactionResponse> getAvailableDeliveries(Pageable pageable) {
        // Just return pending transactions where volunteer is null for demo purposes
        // Custom repository query is needed in production
        return transactionRepository.findAll(pageable)
            .map(this::mapToResponse); // This is a mock implementation
    }

    @Transactional
    public TransactionResponse acceptDelivery(Long transactionId, String volunteerEmail) {
        User volunteer = userRepository.findByEmail(volunteerEmail)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
                
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (transaction.getVolunteer() != null) {
            throw new RuntimeException("Delivery already accepted by another volunteer");
        }

        transaction.setVolunteer(volunteer);
        Transaction saved = transactionRepository.save(transaction);

        // Notify the NGO that a volunteer has accepted the delivery
        notificationService.sendNotification(
            transaction.getNgo(),
            transaction.getListing(),
            NotificationType.VOLUNTEER_ASSIGNED,
            "🚗 " + volunteer.getName() + " has accepted delivery of \"" + transaction.getListing().getFoodName() + "\"! They are on their way to pick it up."
        );

        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponse markDelivered(Long transactionId, String volunteerEmail) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
                
        if (transaction.getVolunteer() == null || !transaction.getVolunteer().getEmail().equals(volunteerEmail)) {
            throw new RuntimeException("Not authorized for this delivery");
        }
        
        if (!transaction.getOtpVerified()) {
            throw new RuntimeException("Must verify OTP before marking as delivered");
        }

        transaction.setDeliveryStatus(DeliveryStatus.DELIVERED);
        transaction.setDeliveredAt(LocalDateTime.now());
        
        Transaction saved = transactionRepository.save(transaction);
        
        // Award points
        gamificationService.addPoints(transaction.getListing().getDonor().getId(), 15);
        gamificationService.addPoints(transaction.getNgo().getId(), 10);
        gamificationService.addPoints(transaction.getVolunteer().getId(), 20);

        // Notify the Donor that their donation successfully reached the NGO
        notificationService.sendNotification(
            transaction.getListing().getDonor(),
            transaction.getListing(),
            NotificationType.DELIVERY_COMPLETE,
            "🌟 Your donation \"" + transaction.getListing().getFoodName() + "\" was successfully delivered to " + transaction.getNgo().getName() + "! You earned 15 points. Thank you for your generosity!"
        );

        // Notify the NGO that the delivery is complete
        notificationService.sendNotification(
            transaction.getNgo(),
            transaction.getListing(),
            NotificationType.DELIVERY_COMPLETE,
            "✅ Delivery of \"" + transaction.getListing().getFoodName() + "\" is complete! Please rate your experience with the donor."
        );

        return mapToResponse(saved);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .listingId(transaction.getListing().getId())
                .foodName(transaction.getListing().getFoodName())
                .donorName(transaction.getListing().getDonor().getName())
                .ngoName(transaction.getNgo().getName())
                .volunteerName(transaction.getVolunteer() != null ? transaction.getVolunteer().getName() : null)
                .claimedAt(transaction.getClaimedAt())
                .deliveredAt(transaction.getDeliveredAt())
                .deliveryStatus(transaction.getDeliveryStatus())
                .otpVerified(transaction.getOtpVerified())
                .donorRating(transaction.getDonorRating())
                .ngoRating(transaction.getNgoRating())
                .build();
    }
}
