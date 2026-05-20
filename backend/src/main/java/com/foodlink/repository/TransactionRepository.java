package com.foodlink.repository;

import com.foodlink.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByNgoIdOrListingDonorId(Long ngoId, Long donorId, Pageable pageable);
}
