package com.salon.repository;

import com.salon.entity.Product;
import com.salon.entity.Salon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findBySalon_IdAndActiveTrue(Long salonId, Pageable pageable);


    Optional<Product> findByIdAndSalon_Id(Long productId, Long salonId);

    List<Product> findBySalon_IdAndActiveTrue(Long salonId);

}
