package com.foodlink.repository;

import com.foodlink.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
    Page<Notification> findByUserIdOrderBySentAtDesc(Long userId, Pageable pageable);
    int countByUserIdAndIsReadFalse(Long userId);
}

