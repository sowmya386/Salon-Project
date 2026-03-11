package com.salon.service;

import com.salon.dto.ProductRequest;
import com.salon.dto.ProductResponse;
import com.salon.entity.Product;
import com.salon.entity.Salon;
import com.salon.repository.ProductRepository;
import com.salon.repository.SalonRepository;
import com.salon.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SalonRepository salonRepository;

    public ProductService(ProductRepository productRepository,
                          SalonRepository salonRepository) {
        this.productRepository = productRepository;
        this.salonRepository = salonRepository;
    }

    // ADMIN
    public ProductResponse createProduct(ProductRequest request) {

        Long salonId = SecurityUtil.getCurrentSalonId();
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSalon(salon);

        product = productRepository.save(product);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }

    // CUSTOMER + ADMIN
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {

        Long salonId = SecurityUtil.getCurrentSalonId();
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon not found"));

        return productRepository
                .findBySalon_IdAndActiveTrue(salonId, pageable)
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStock()
                ));
    }
}
