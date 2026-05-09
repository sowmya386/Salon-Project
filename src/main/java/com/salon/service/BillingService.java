package com.salon.service;

import com.salon.audit.AuditAction;
import com.salon.audit.AuditLogService;
import com.salon.dto.*;
import com.salon.entity.*;
import com.salon.repository.*;
import com.salon.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final BookingRepository bookingRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final SalonService salonService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;
    private final ProductSaleRepository productSaleRepository;

    public BillingService(
            InvoiceRepository invoiceRepository,
            InvoiceItemRepository invoiceItemRepository,
            BookingRepository bookingRepository,
            ProductRepository productRepository,
            ServiceRepository serviceRepository,
            SalonService salonService,
            AuditLogService auditLogService,
            UserRepository userRepository,
            ProductSaleRepository productSaleRepository) {

        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.bookingRepository = bookingRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.salonService = salonService;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
        this.productSaleRepository = productSaleRepository;
    }

    @Transactional
    public InvoiceResponse createInvoice(BillingRequest request) {

        Salon salon = salonService.getCurrentSalon();
        String salonName = salon.getName();
        Long customerId = SecurityUtil.getCurrentUserId();
        User customer = userRepository.findById(customerId).get();
        
        
        Invoice invoice = new Invoice();
        invoice.setSalonName(salonName);
        invoice.setPaymentMode(PaymentMode.valueOf(request.getPaymentMode()));
        invoice.setCustomer(customer);  
        
     

        // Attach booking if present
        if (request.getBookingId() != null) {
        	Booking booking = bookingRepository
        	        .findByIdAndSalonName(request.getBookingId(), salonName)
        	        .orElseThrow(() -> new RuntimeException("Booking not found"));
        	

            if (booking.getStatus() != BookingStatus.COMPLETED) {
                throw new RuntimeException("Only completed bookings can be billed");
            }

            invoice.setCustomer(booking.getCustomer());
            invoice.setBooking(booking);
        }

        invoice = invoiceRepository.save(invoice);

        double total = 0;
        List<InvoiceItemResponse> responseItems = new ArrayList<>();

        for (BillingItemRequest itemReq : request.getItems()) {

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);

            if (itemReq.getItemType().equals("SERVICE")) {

            	Service service = serviceRepository
            	        .findByIdAndSalonName(itemReq.getItemId(), salonName)
            	        .orElseThrow(() -> new RuntimeException("Service not found"));

                item.setItemName(service.getName());
                item.setPrice(service.getPrice());
                item.setQuantity(1);
                item.setItemType(ItemType.SERVICE);

            } else if (itemReq.getItemType().equals("PRODUCT")) {

            	Product product = productRepository
            	        .findByIdAndSalonName(itemReq.getItemId(), salonName)
            	        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (product.getStock() < itemReq.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product");
                }

                product.setStock(product.getStock() - itemReq.getQuantity());
                productRepository.save(product);

                item.setItemName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(itemReq.getQuantity());
                item.setItemType(ItemType.PRODUCT);

                ProductSale sale = new ProductSale();
                sale.setProduct(product);
                sale.setInvoice(invoice);
                sale.setQuantity(itemReq.getQuantity());
                sale.setPricePerUnit(product.getPrice());
                sale.setTotalRevenue(product.getPrice() * itemReq.getQuantity());
                sale.setSalonName(salonName);
                productSaleRepository.save(sale);

            } else {
                throw new RuntimeException("Invalid item type");
            }

            double amount = item.getPrice() * item.getQuantity();
            item.setAmount(amount);

            invoiceItemRepository.save(item);
            total += amount;

            responseItems.add(new InvoiceItemResponse(
                    item.getItemName(),
                    item.getItemType().name(),
                    item.getPrice(),
                    item.getQuantity(),
                    amount
            ));
        }

        invoice.setTotalAmount(total);
        invoiceRepository.save(invoice);

        auditLogService.log(
                AuditAction.CREATE_INVOICE,
                "Invoice created with ID " + invoice.getId()
        );

        return new InvoiceResponse(
                invoice.getId(),
                total,
                invoice.getPaymentMode().name(),
                invoice.getCreatedAt(),
                responseItems,
                invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "Customer",
                invoice.getCustomer() != null ? invoice.getCustomer().getEmail() : "",
                invoice.getCustomer() != null && invoice.getCustomer().getPhone() != null ? invoice.getCustomer().getPhone() : ""
        );
    }

    @Transactional
    public InvoiceResponse checkoutProducts(CustomerCheckoutRequest request) {
        Salon salon = salonService.getCurrentSalon();
        String salonName = salon.getName();
        Long customerId = SecurityUtil.getCurrentUserId();
        User customer = userRepository.findById(customerId).get();
        
        Invoice invoice = new Invoice();
        invoice.setSalonName(salonName);
        invoice.setCustomer(customer);  
        
        // Update customer's address and pincode from checkout
        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            customer.setHomeAddress(request.getAddress());
            customer.setPincode(request.getPincode());
            userRepository.save(customer);
        }
        
        String payMethod = request.getPaymentMethod();
        try {
            if (payMethod.equalsIgnoreCase("Google Pay")) invoice.setPaymentMode(PaymentMode.UPI);
            else if (payMethod.equalsIgnoreCase("PhonePe")) invoice.setPaymentMode(PaymentMode.UPI);
            else if (payMethod.equalsIgnoreCase("Card")) invoice.setPaymentMode(PaymentMode.CARD);
            else if (payMethod.equalsIgnoreCase("Cash on Delivery")) invoice.setPaymentMode(PaymentMode.CASH);
            else invoice.setPaymentMode(PaymentMode.valueOf(payMethod.toUpperCase()));
        } catch (Exception e) {
            invoice.setPaymentMode(PaymentMode.UPI); // fallback
        }

        invoice = invoiceRepository.save(invoice);

        double total = 0;
        List<InvoiceItemResponse> responseItems = new ArrayList<>();

        for (CustomerCheckoutRequest.CheckoutItem itemReq : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);

            Product product = productRepository
                    .findByIdAndSalonName(itemReq.getProductId(), salonName)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product " + product.getName());
            }

            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);

            item.setItemName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(itemReq.getQuantity());
            item.setItemType(ItemType.PRODUCT);

            ProductSale sale = new ProductSale();
            sale.setProduct(product);
            sale.setInvoice(invoice);
            sale.setQuantity(itemReq.getQuantity());
            sale.setPricePerUnit(product.getPrice());
            sale.setTotalRevenue(product.getPrice() * itemReq.getQuantity());
            sale.setSalonName(salonName);
            productSaleRepository.save(sale);

            double amount = item.getPrice() * item.getQuantity();
            item.setAmount(amount);

            invoiceItemRepository.save(item);
            total += amount;

            responseItems.add(new InvoiceItemResponse(
                    item.getItemName(),
                    item.getItemType().name(),
                    item.getPrice(),
                    item.getQuantity(),
                    amount
            ));
        }

        invoice.setTotalAmount(total);
        invoiceRepository.save(invoice);

        auditLogService.log(
                AuditAction.CREATE_INVOICE,
                "Customer " + customer.getFullName() + " checked out products, Invoice ID " + invoice.getId()
        );

        int pointsEarned = (int) total;
        if (pointsEarned > 0) {
            customer.setLoyaltyPoints((customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0) + pointsEarned);
            userRepository.save(customer);
        }

        return new InvoiceResponse(
                invoice.getId(),
                total,
                invoice.getPaymentMode().name(),
                invoice.getCreatedAt(),
                responseItems,
                invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "Customer",
                invoice.getCustomer() != null ? invoice.getCustomer().getEmail() : "",
                invoice.getCustomer() != null && invoice.getCustomer().getPhone() != null ? invoice.getCustomer().getPhone() : ""
        );
    }

    public Page<InvoiceResponse> getSalonInvoices(Pageable pageable) {
        String salonName = salonService.getCurrentSalon().getName();

        return invoiceRepository.findBySalonName(salonName, pageable)
                .map(invoice -> new InvoiceResponse(
                        invoice.getId(),
                        invoice.getTotalAmount(),
                        invoice.getPaymentMode().name(),
                        invoice.getCreatedAt(),
                        invoice.getItems().stream()
                                .map(i -> new InvoiceItemResponse(
                                        i.getItemName(),
                                        i.getItemType().name(),
                                        i.getPrice(),
                                        i.getQuantity(),
                                        i.getAmount()
                                ))
                                .toList(),
                        invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "Customer",
                        invoice.getCustomer() != null ? invoice.getCustomer().getEmail() : "",
                        invoice.getCustomer() != null && invoice.getCustomer().getPhone() != null ? invoice.getCustomer().getPhone() : ""
                ));
    }
    public Page<InvoiceResponse> getCustomerInvoices(Pageable pageable) {
        Long customerId = SecurityUtil.getCurrentUserId();
        String salonName = salonService.getCurrentSalon().getName();

        return invoiceRepository
                .findByCustomer_IdAndSalonName(customerId, salonName, pageable)
                .map(invoice -> new InvoiceResponse(
                        invoice.getId(),
                        invoice.getTotalAmount(),
                        invoice.getPaymentMode().name(),
                        invoice.getCreatedAt(),
                        invoice.getItems().stream()
                                .map(i -> new InvoiceItemResponse(
                                        i.getItemName(),
                                        i.getItemType().name(),
                                        i.getPrice(),
                                        i.getQuantity(),
                                        i.getAmount()
                                ))
                                .toList(),
                        invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "Customer",
                        invoice.getCustomer() != null ? invoice.getCustomer().getEmail() : "",
                        invoice.getCustomer() != null && invoice.getCustomer().getPhone() != null ? invoice.getCustomer().getPhone() : ""
                ));
    }

    @Transactional
    public InvoiceResponse createManualInvoice(ManualInvoiceRequest request) {
        Salon salon = salonService.getCurrentSalon();
        String salonName = salon.getName();

        Invoice invoice = new Invoice();
        invoice.setSalonName(salonName);
        invoice.setPaymentMode(PaymentMode.valueOf(request.getPaymentMode() != null ? request.getPaymentMode() : "CASH"));
        
        // Match existing user by phone if possible
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isBlank()) {
            User existingUser = userRepository.findFirstByEmail(request.getCustomerPhone() + "@placeholder.com").orElse(null);
            if (existingUser != null) {
                invoice.setCustomer(existingUser);
            } else {
                invoice.setCustomerName(request.getCustomerName());
                invoice.setCustomerPhone(request.getCustomerPhone());
            }
        } else {
            invoice.setCustomerName(request.getCustomerName());
        }

        invoice.setInvoiceNumber("INV-" + java.time.Year.now().getValue() + "-" + (int)(Math.random() * 10000));
        invoice.setStatus(InvoiceStatus.PAID);
        invoice = invoiceRepository.save(invoice);

        double subtotal = 0;
        List<InvoiceItemResponse> responseItems = new ArrayList<>();

        for (BillingItemRequest itemReq : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);

            if ("SERVICE".equals(itemReq.getItemType())) {
                Service service = serviceRepository.findByIdAndSalonName(itemReq.getItemId(), salonName)
                        .orElseThrow(() -> new RuntimeException("Service not found"));
                item.setItemName(service.getName());
                item.setPrice(service.getPrice());
                item.setQuantity(1);
                item.setItemType(ItemType.SERVICE);
            } else if ("PRODUCT".equals(itemReq.getItemType())) {
                Product product = productRepository.findByIdAndSalonName(itemReq.getItemId(), salonName)
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                if (product.getStock() < itemReq.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product");
                }
                product.setStock(product.getStock() - itemReq.getQuantity());
                productRepository.save(product);

                item.setItemName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(itemReq.getQuantity());
                item.setItemType(ItemType.PRODUCT);

                ProductSale sale = new ProductSale();
                sale.setProduct(product);
                sale.setInvoice(invoice);
                sale.setQuantity(itemReq.getQuantity());
                sale.setPricePerUnit(product.getPrice());
                sale.setTotalRevenue(product.getPrice() * itemReq.getQuantity());
                sale.setSalonName(salonName);
                productSaleRepository.save(sale);
            } else {
                throw new RuntimeException("Invalid item type");
            }

            double amount = item.getPrice() * item.getQuantity();
            item.setAmount(amount);
            invoiceItemRepository.save(item);
            subtotal += amount;

            responseItems.add(new InvoiceItemResponse(item.getItemName(), item.getItemType().name(), item.getPrice(), item.getQuantity(), amount));
        }

        double discount = request.getDiscount() != null ? request.getDiscount() : 0.0;
        double taxableAmount = subtotal - discount;
        double gstTotal = taxableAmount * 0.18; // 18% GST example
        double totalAmount = taxableAmount + gstTotal;

        invoice.setSubtotal(subtotal);
        invoice.setDiscount(discount);
        invoice.setGstTotal(gstTotal);
        invoice.setTotalAmount(totalAmount);
        invoiceRepository.save(invoice);

        auditLogService.log(AuditAction.CREATE_INVOICE, "Manual Invoice created with ID " + invoice.getInvoiceNumber());

        if (invoice.getCustomer() != null) {
            int pointsEarned = (int) totalAmount;
            if (pointsEarned > 0) {
                User customer = invoice.getCustomer();
                customer.setLoyaltyPoints((customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0) + pointsEarned);
                userRepository.save(customer);
            }
        }

        return new InvoiceResponse(
                invoice.getId(),
                totalAmount,
                invoice.getPaymentMode().name(),
                invoice.getCreatedAt(),
                responseItems,
                invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : invoice.getCustomerName(),
                invoice.getCustomer() != null ? invoice.getCustomer().getEmail() : "",
                invoice.getCustomer() != null && invoice.getCustomer().getPhone() != null ? invoice.getCustomer().getPhone() : invoice.getCustomerPhone()
        );
    }
}
