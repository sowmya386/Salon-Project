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

	List<Service> findBySalonNameAndActiveTrue(String salonName);

	Page<Service> findBySalonNameAndActiveTrue(String salonName, Pageable pageable);

    Optional<Service> findByIdAndSalonName(Long id, String salonName);

    // ================= TOP SERVICES (DASHBOARD) =================
    @Query("""
    	    SELECT new com.salon.dto.TopItemResponse(
    	        s.name,
    	        COUNT(b)
    	    )
    	    FROM Booking b
    	    JOIN b.services s
    	    WHERE b.salonName = :salonName
    	      AND b.status = 'COMPLETED'
    	    GROUP BY s.name
    	    ORDER BY COUNT(b) DESC
    	""")
    	List<TopItemResponse> findTopServices(@org.springframework.data.repository.query.Param("salonName") String salonName);

}
