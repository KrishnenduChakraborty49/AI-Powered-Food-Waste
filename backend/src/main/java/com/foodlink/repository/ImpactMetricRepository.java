package com.foodlink.repository;

import com.foodlink.model.ImpactMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpactMetricRepository extends JpaRepository<ImpactMetric, Long> {
}
