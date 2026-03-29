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

    public BillingService(
            InvoiceRepository invoiceRepository,
            InvoiceItemRepository invoiceItemRepository,
            BookingRepository bookingRepository,
            ProductRepository productRepository,
            ServiceRepository serviceRepository,
            SalonService salonService,
            AuditLogService auditLogService,
            UserRepository userRepository) {

        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.bookingRepository = bookingRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.salonService = salonService;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
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
                responseItems
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

        return new InvoiceResponse(
                invoice.getId(),
                total,
                invoice.getPaymentMode().name(),
                invoice.getCreatedAt(),
                responseItems
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
                                .toList()
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
                                .toList()
                ));
    }

}
