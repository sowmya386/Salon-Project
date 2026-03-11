package com.salon.repository;

import com.salon.entity.Service;
import com.salon.dto.TopItemResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

	List<Service> findBySalon_IdAndActiveTrue(Long salonId);

	Page<Service> findBySalon_IdAndActiveTrue(Long salonId, Pageable pageable);

    Optional<Service> findByIdAndSalonId(Long id, Long salonId);

    // ================= TOP SERVICES (DASHBOARD) =================
    @Query("""
    	    SELECT new com.salon.dto.TopItemResponse(
    	        b.service.name,
    	        COUNT(b)
    	    )
    	    FROM Booking b
    	    WHERE b.salon.id = :salonId
    	      AND b.status = 'COMPLETED'
    	    GROUP BY b.service.name
    	    ORDER BY COUNT(b) DESC
    	""")
    	List<TopItemResponse> findTopServices(Long salonId);

}
