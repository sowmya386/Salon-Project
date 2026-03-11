package com.salon.repository;

import com.salon.dto.TopItemResponse;
import com.salon.entity.InvoiceItem;
import com.salon.entity.Salon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
	
	@Query("""
		    SELECT new com.salon.dto.TopItemResponse(
		        ii.itemName,
		        SUM(ii.quantity)
		    )
		    FROM InvoiceItem ii
		    WHERE ii.invoice.salon.id = :salonId
		      AND ii.itemType = 'PRODUCT'
		    GROUP BY ii.itemName
		    ORDER BY SUM(ii.quantity) DESC
		""")
		List<TopItemResponse> findTopProducts(Long salonId);

}
