package com.salon.repository;

import com.salon.entity.ApprovalStatus;
import com.salon.entity.Salon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonRepository extends JpaRepository<Salon, Long> {
	
	Optional<Salon> findByNameIgnoreCase(String name);
	
	List<Salon> findByApprovalStatus(ApprovalStatus status);

}