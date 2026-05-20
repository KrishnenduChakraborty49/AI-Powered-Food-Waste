package com.foodlink.service;

import com.foodlink.dto.TransactionResponse;
import com.foodlink.model.FoodListing;
import com.foodlink.model.Transaction;
import com.foodlink.model.User;
import com.foodlink.model.enums.ListingStatus;
import com.foodlink.repository.FoodListingRepository;
import com.foodlink.repository.NotificationRepository;
import com.foodlink.repository.TransactionRepository;
import com.foodlink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FoodListingRepository listingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private TransactionService transactionService;

    private User ngo;
    private User donor;
    private FoodListing availableListing;
    private FoodListing claimedListing;

    @BeforeEach
    void setUp() {
        ngo = new User();
        ngo.setId(1L);
        ngo.setEmail("ngo@test.com");
        ngo.setName("NGO Test");

        donor = new User();
        donor.setId(2L);
        donor.setName("Donor Test");

        availableListing = new FoodListing();
        availableListing.setId(10L);
        availableListing.setStatus(ListingStatus.AVAILABLE);
        availableListing.setDonor(donor);

        claimedListing = new FoodListing();
        claimedListing.setId(20L);
        claimedListing.setStatus(ListingStatus.CLAIMED);
        claimedListing.setDonor(donor);
    }

    @Test
    void testClaimListing_Success() {
        when(userRepository.findByEmail("ngo@test.com")).thenReturn(Optional.of(ngo));
        when(listingRepository.findById(10L)).thenReturn(Optional.of(availableListing));
        
        Transaction mockSavedTransaction = new Transaction();
        mockSavedTransaction.setId(100L);
        mockSavedTransaction.setListing(availableListing);
        mockSavedTransaction.setNgo(ngo);
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockSavedTransaction);

        TransactionResponse response = transactionService.claimListing(10L, "ngo@test.com");

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(ListingStatus.CLAIMED, availableListing.getStatus());
        
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(smsService, times(1)).sendSms(any(), any());
    }

    @Test
    void testClaimListing_AlreadyClaimed_ThrowsException() {
        when(userRepository.findByEmail("ngo@test.com")).thenReturn(Optional.of(ngo));
        when(listingRepository.findById(20L)).thenReturn(Optional.of(claimedListing));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.claimListing(20L, "ngo@test.com");
        });

        assertEquals("Listing is not available", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
