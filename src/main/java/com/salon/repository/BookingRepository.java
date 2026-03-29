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

	 List<Booking> findByCustomer_IdAndSalonName(Long customerId, String salonName);

	    Page<Booking> findByCustomer_IdAndSalonName(
	            Long customerId,
	            String salonName,
	            Pageable pageable
	    );

	    // ADMIN — list salon bookings
	    List<Booking> findBySalonName(String salonName);

	    Page<Booking> findBySalonName(String salonName, Pageable pageable);

	    // COMMON — find single booking inside salon
	    Optional<Booking> findByIdAndSalonName(Long bookingId, String salonName);
	   
    
	    Optional<Booking> findTopByCustomer_IdAndSalonNameOrderByAppointmentTimeDesc(
	            Long customerId,
	            String salonName
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
    	        WHERE b.salonName = :salonName
    	          AND b.status = 'COMPLETED'
    	        GROUP BY b.customer.id, b.customer.fullName, b.customer.email, b.customer.phone
    	        HAVING MAX(b.appointmentTime) < :cutoffDate
    	    """)
    	    Page<Object[]> findInactiveCustomers(
    	            String salonName,
    	            @org.springframework.data.repository.query.Param("cutoffDate") LocalDateTime cutoffDate,
    	            Pageable pageable
    	    );

	    // ADMIN — directory of customers with booking stats
	    @Query("""
	            SELECT
	                b.customer.id,
	                b.customer.fullName,
	                b.customer.email,
	                b.customer.phone,
	                MAX(b.appointmentTime),
	                COUNT(b)
	            FROM Booking b
	            WHERE b.salonName = :salonName
	              AND b.status = 'COMPLETED'
	            GROUP BY b.customer.id, b.customer.fullName, b.customer.email, b.customer.phone
	            """)
	    Page<Object[]> findCustomerStats(
	            String salonName,
	            Pageable pageable
	    );
    	
    	    long countBySalonNameAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThan(
    	            String salonName,
    	            LocalDateTime start,
    	            LocalDateTime end
    	    );

    	    long countBySalonNameAndStatus(String salonName, BookingStatus status);
            
            List<Booking> findBySalonNameAndAppointmentTimeBetweenAndStatus(
                    String salonName,
                    LocalDateTime start,
                    LocalDateTime end,
                    BookingStatus status
            );
}
