package com.salon.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.salon.exception.BadRequestException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Formatter;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public String createOrder(Double amountInRupees) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject options = new JSONObject();
            // Razorpay expects amount in smallest unit (paise)
            int amountInPaise = (int) (amountInRupees * 100);
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = client.orders.create(options);
            return order.get("id");
        } catch (RazorpayException e) {
            throw new RuntimeException("Error creating Razorpay order: " + e.getMessage());
        }
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            String expectedSignature = formatter.toString();
            formatter.close();

            return expectedSignature.equals(signature);
        } catch (Exception e) {
            throw new BadRequestException("Failed to verify payment signature: " + e.getMessage());
        }
    }
}
