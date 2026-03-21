package com.salon.repository;

import com.salon.entity.Product;
import com.salon.entity.Salon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findBySalonNameAndActiveTrue(String salonName, Pageable pageable);


    Optional<Product> findByIdAndSalonName(Long productId, String salonName);

    List<Product> findBySalonNameAndActiveTrue(String salonName);

    @org.springframework.data.jpa.repository.Query("""
            SELECT p FROM Product p
            WHERE p.salonName = :salonName AND p.active = true
              AND p.stock <= p.lowStockThreshold AND p.stock > 0
        """)
    List<Product> findLowStockProducts(@org.springframework.data.repository.query.Param("salonName") String salonName);

}
