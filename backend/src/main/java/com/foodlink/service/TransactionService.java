package com.foodlink.service;

import com.foodlink.dto.TransactionResponse;
import com.foodlink.model.FoodListing;
import com.foodlink.model.Transaction;
import com.foodlink.model.User;
import com.foodlink.model.enums.DeliveryStatus;
import com.foodlink.model.enums.ListingStatus;
import com.foodlink.model.enums.NotificationType;
import com.foodlink.repository.FoodListingRepository;
import com.foodlink.repository.TransactionRepository;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FoodListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public TransactionResponse claimListing(Long listingId, String ngoEmail) {
        User ngo = userRepository.findByEmail(ngoEmail)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        FoodListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new RuntimeException("Listing is not available");
        }

        listing.setStatus(ListingStatus.CLAIMED);
        listingRepository.save(listing);

        String otp = String.format("%06d", new Random().nextInt(999999));

        Transaction transaction = Transaction.builder()
                .listing(listing)
                .ngo(ngo)
                .deliveryStatus(DeliveryStatus.PENDING)
                .otpCode(otp)
                .otpVerified(false)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Notify the Donor that their food has been claimed
        notificationService.sendNotification(
            listing.getDonor(),
            listing,
            NotificationType.CLAIM_CONFIRMED,
            "🎉 Great news! Your donation \"" + listing.getFoodName() + "\" has been claimed by " + ngo.getName() + ". A volunteer will be assigned soon."
        );

        // Notify the NGO with their OTP
        notificationService.sendNotification(
            ngo,
            listing,
            NotificationType.CLAIM_CONFIRMED,
            "✅ You've claimed \"" + listing.getFoodName() + "\"! Your pickup OTP is: " + otp + ". Share this with the volunteer at pickup."
        );

        // Send Real-world SMS notification with OTP to the NGO
        String ngoPhone = ngo.getPhone() != null ? ngo.getPhone() : "+15550000000";
        String smsBody = "FoodLink: You claimed '" + listing.getFoodName() + "'. Your secure Pickup OTP is " + otp + ". Give this to the volunteer when they arrive.";
        smsService.sendSms(ngoPhone, smsBody);

        return mapToResponse(savedTransaction);
    }

    public Page<TransactionResponse> getMyTransactions(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByNgoIdOrListingDonorId(user.getId(), user.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public TransactionResponse confirmOtp(Long transactionId, String otpCode) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getOtpCode().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP");
        }

        transaction.setOtpVerified(true);
        transaction.setDeliveryStatus(DeliveryStatus.IN_TRANSIT);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Notify Donor that volunteer has picked up the food
        notificationService.sendNotification(
            transaction.getListing().getDonor(),
            transaction.getListing(),
            NotificationType.VOLUNTEER_ASSIGNED,
            "🚚 A volunteer has picked up your \"" + transaction.getListing().getFoodName() + "\" and is on the way to " + transaction.getNgo().getName() + "!"
        );

        return mapToResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse rateTransaction(Long transactionId, Integer rating, String email) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (transaction.getListing().getDonor().getId().equals(user.getId())) {
            // Donor is rating the NGO
            transaction.setNgoRating(rating);
            // Update NGO's trust score
            updateTrustScore(transaction.getNgo(), rating);
        } else if (transaction.getNgo().getId().equals(user.getId())) {
            // NGO is rating the Donor
            transaction.setDonorRating(rating);
            // Update Donor's trust score
            updateTrustScore(transaction.getListing().getDonor(), rating);
        } else {
            throw new RuntimeException("Not authorized to rate this transaction");
        }

        return mapToResponse(transactionRepository.save(transaction));
    }

    /**
     * Dynamically recalculates and saves a user's trust score using a weighted average.
     * Formula: newScore = ((oldScore * totalRatings) + newRating) / (totalRatings + 1)
     */
    private void updateTrustScore(User targetUser, int newRating) {
        BigDecimal oldScore = targetUser.getTrustScore();
        int totalRatings = targetUser.getTotalRatings();

        BigDecimal weightedSum = oldScore.multiply(BigDecimal.valueOf(totalRatings))
                .add(BigDecimal.valueOf(newRating));
        BigDecimal newScore = weightedSum.divide(BigDecimal.valueOf(totalRatings + 1), 2, RoundingMode.HALF_UP);

        targetUser.setTrustScore(newScore);
        targetUser.setTotalRatings(totalRatings + 1);
        userRepository.save(targetUser);
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
