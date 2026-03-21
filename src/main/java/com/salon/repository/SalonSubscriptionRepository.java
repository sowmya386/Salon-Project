package com.salon.repository;

import com.salon.entity.SalonSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalonSubscriptionRepository extends JpaRepository<SalonSubscription, Long> {
    Optional<SalonSubscription> findBySalonNameAndActiveTrue(String salonName);
}
