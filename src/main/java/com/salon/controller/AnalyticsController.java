package com.salon.controller;

import com.salon.repository.ProductSaleRepository;
import com.salon.security.SecurityUtil;
import com.salon.service.SalonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final ProductSaleRepository productSaleRepository;
    private final SalonService salonService;

    public AnalyticsController(ProductSaleRepository productSaleRepository, SalonService salonService) {
        this.productSaleRepository = productSaleRepository;
        this.salonService = salonService;
    }

    @GetMapping("/product-sales")
    public ResponseEntity<List<Map<String, Object>>> getProductSales() {
        String salonName = salonService.getCurrentSalon().getName();
        List<Object[]> rawSales = productSaleRepository.getSalesAnalyticsBySalon(salonName);
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] row : rawSales) {
            Map<String, Object> map = new HashMap<>();
            map.put("productName", row[0]);
            map.put("unitsSold", row[1]);
            map.put("totalRevenue", row[2]);
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
}
