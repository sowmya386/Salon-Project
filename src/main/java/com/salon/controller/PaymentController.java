package com.salon.controller;

import com.salon.dto.RazorpayOrderRequest;
import com.salon.dto.RazorpayOrderResponse;
import com.salon.dto.RazorpayVerifyRequest;
import com.salon.service.RazorpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final RazorpayService razorpayService;

    public PaymentController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<RazorpayOrderResponse> createOrder(@RequestBody RazorpayOrderRequest request) {
        String orderId = razorpayService.createOrder(request.getAmount());
        return ResponseEntity.ok(new RazorpayOrderResponse(orderId));
    }

    @PostMapping("/verify-signature")
    public ResponseEntity<String> verifySignature(@RequestBody RazorpayVerifyRequest request) {
        boolean isValid = razorpayService.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (isValid) {
            return ResponseEntity.ok("Payment successful");
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }
}
