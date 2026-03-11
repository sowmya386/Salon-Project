package com.salon.repository;

import com.salon.entity.*;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	 List<Booking> findByCustomerIdAndSalonId(Long customerId, Long salonId);

	    Page<Booking> findByCustomerIdAndSalonId(
	            Long customerId,
	            Long salonId,
	            Pageable pageable
	    );

	    // ADMIN — list salon bookings
	    List<Booking> findBySalonId(Long salonId);

	    Page<Booking> findBySalonId(Long salonId, Pageable pageable);

	    // COMMON — find single booking inside salon
	    Optional<Booking> findByIdAndSalonId(Long bookingId, Long salonId);
	   
    
	    Optional<Booking> findTopByCustomer_IdAndSalon_IdOrderByAppointmentTimeDesc(
	            Long customerId,
	            Long salonId
	    );
     @Query("""
    	        SELECT 
    	            b.customer.id,
    	            b.customer.fullName,
    	            b.customer.email,
    	            b.customer.phone,
    	            MAX(b.appointmentTime),
    	            COUNT(b)
    	        FROM Booking b
    	        WHERE b.salon.id = :salonId
    	          AND b.status = 'COMPLETED'
    	        GROUP BY b.customer.id, b.customer.fullName, b.customer.email, b.customer.phone
    	        HAVING MAX(b.appointmentTime) < :cutoffDate
    	    """)
    	    Page<Object[]> findInactiveCustomers(
    	            Long salonId,
    	            LocalDateTime cutoffDate,
    	            Pageable pageable
    	    );
    	
    	    long countBySalon_IdAndAppointmentTimeBetween(
    	            Long salonId,
    	            LocalDateTime start,
    	            LocalDateTime end
    	    );

    	    long countBySalon_IdAndStatus(Long salonId, BookingStatus status);

    
}
