package com.salon.repository;

import com.salon.entity.ProductSale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductSaleRepository extends JpaRepository<ProductSale, Long> {
    Page<ProductSale> findBySalonName(String salonName, Pageable pageable);

    @Query("SELECT ps.product.name, SUM(ps.quantity), SUM(ps.totalRevenue) " +
           "FROM ProductSale ps WHERE ps.salonName = :salonName " +
           "GROUP BY ps.product.name ORDER BY SUM(ps.totalRevenue) DESC")
    List<Object[]> getSalesAnalyticsBySalon(@Param("salonName") String salonName);
}
