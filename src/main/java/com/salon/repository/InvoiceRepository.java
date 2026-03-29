package com.salon.repository;

import com.salon.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.salon.entity.Salon;
import com.salon.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	
	Optional<Invoice> findByIdAndSalonName(Long invoiceId, String salonName);

	List<Invoice> findBySalonName(String salonName);

	Page<Invoice> findBySalonName(String salonName, Pageable pageable);

	List<Invoice> findByCustomer_IdAndSalonName(Long customerId, String salonName);
	    
	Page<Invoice> findByCustomer_IdAndSalonName(
	        Long customerId,
	        String salonName,
	        Pageable pageable
	);
	
	@Query("""
	    SELECT COALESCE(SUM(i.totalAmount), 0)
	    FROM Invoice i
	    WHERE i.salonName = :salonName
	""")
	double getTotalRevenue(@Param("salonName") String salonName);

	@Query("""
	    SELECT COALESCE(SUM(i.totalAmount), 0)
	    FROM Invoice i
	    WHERE i.salonName = :salonName
	        AND i.createdAt >= :start AND i.createdAt < :end
	""")
	double getRevenueBetween(
	    @Param("salonName") String salonName, 
	    @Param("start") LocalDateTime start, 
	    @Param("end") LocalDateTime end
	);

	@Query("""
	    SELECT i.customer.id, i.customer.fullName, i.customer.email,
	           SUM(i.totalAmount), COUNT(i)
	    FROM Invoice i
	    WHERE i.salonName = :salonName AND i.customer IS NOT NULL
	    GROUP BY i.customer.id, i.customer.fullName, i.customer.email
	    ORDER BY SUM(i.totalAmount) DESC
	""")
	List<Object[]> findTopCustomersBySpend(@Param("salonName") String salonName, Pageable pageable);
}
