package com.salon.service;

import com.salon.dto.ProductRequest;
import com.salon.dto.ProductResponse;
import com.salon.entity.Product;
import com.salon.entity.Salon;
import com.salon.repository.ProductRepository;
import com.salon.audit.AuditLogService;
import com.salon.audit.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SalonService salonService;
    private final AuditLogService auditLogService;

    public ProductService(ProductRepository productRepository,
                          SalonService salonService,
                          AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.salonService = salonService;
        this.auditLogService = auditLogService;
    }

    // ADMIN
    public ProductResponse createProduct(ProductRequest request) {

        Salon salon = salonService.getCurrentSalon();

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSalonName(salon.getName());

        product = productRepository.save(product);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }

    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Salon salon = salonService.getCurrentSalon();
        Product product = productRepository.findByIdAndSalonName(productId, salon.getName())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product = productRepository.save(product);
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock());
    }

    // CUSTOMER + ADMIN
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {

        String salonName = salonService.getCurrentSalon().getName();

        return productRepository
                .findBySalonNameAndActiveTrue(salonName, pageable)
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStock()
                ));
    }

    // ADMIN ONLY: soft-delete by deactivating product
    public ProductResponse deactivateProduct(Long productId) {
        Salon salon = salonService.getCurrentSalon();

        Product product = productRepository.findByIdAndSalonName(productId, salon.getName())
                .orElseThrow(() -> new RuntimeException("Product not found in this salon"));

        product.setActive(false);
        product = productRepository.save(product);

        auditLogService.log(
                AuditAction.DEACTIVATE_PRODUCT,
                "Admin deactivated product ID " + product.getId()
        );

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
