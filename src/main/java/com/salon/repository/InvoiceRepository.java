package com.salon.repository;

import com.salon.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.salon.entity.Salon;
import com.salon.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	
	 Optional<Invoice> findByIdAndSalon_Id(Long invoiceId, Long salonId);

	    List<Invoice> findBySalon_Id(Long salonId);

	    Page<Invoice> findBySalon_Id(Long salonId, Pageable pageable);

	    List<Invoice> findByCustomer_IdAndSalon_Id(Long customerId, Long salonId);
	    
	    

	    Page<Invoice> findByCustomer_IdAndSalon_Id(
	            Long customerId,
	            Long salonId,
	            Pageable pageable
	    );
	    @Query("""
	    	    SELECT COALESCE(SUM(i.totalAmount), 0)
	    	    FROM Invoice i
	    	    WHERE i.salon.id = :salonId
	    	""")
	    	double getTotalRevenue(Long salonId);
}
