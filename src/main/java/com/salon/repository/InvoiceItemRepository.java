package com.salon.repository;

import com.salon.dto.TopItemResponse;
import com.salon.entity.InvoiceItem;
import com.salon.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    @Query("""
        SELECT new com.salon.dto.TopItemResponse(
            ii.itemName,
            SUM(ii.quantity)
        )
        FROM InvoiceItem ii
        WHERE ii.invoice.salonName = :salonName
          AND ii.itemType = com.salon.entity.ItemType.PRODUCT
        GROUP BY ii.itemName
        ORDER BY SUM(ii.quantity) DESC
    """)
    List<TopItemResponse> findTopProducts(@Param("salonName") String salonName);
}