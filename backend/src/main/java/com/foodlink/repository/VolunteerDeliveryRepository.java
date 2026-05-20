package com.foodlink.repository;

import com.foodlink.model.VolunteerDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerDeliveryRepository extends JpaRepository<VolunteerDelivery, Long> {
}
