package com.andi.carikopi.feature.payment;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopRepository;
import com.andi.carikopi.feature.order.Order;
import com.andi.carikopi.feature.order.OrderRepository;

@RestController
@RequestMapping("/api/public/webhooks/xendit")
public class XenditWebhookController {

    @Autowired
    private CoffeeShopRepository shopRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<Void> handle(@RequestHeader("x-callback-token") String incomingToken, 
                                       @RequestBody Map<String, Object> payload) {
        
        // Extract nested data object
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        if (data == null) {
            System.err.println("Webhook payload missing 'data' object");
            return ResponseEntity.ok().build();
        }

        String referenceId = (String) data.get("reference_id");
        if (referenceId == null) {
            referenceId = (String) data.get("external_id"); // Fallback check
        }
        
        if (referenceId == null) {
            System.err.println("Webhook data missing id reference");
            return ResponseEntity.ok().build();
        }

        // Split using the new colon delimiter
        String[] parts = referenceId.split(":");
        if (parts.length < 3) {
            System.err.println("Malformed reference_id: " + referenceId);
            return ResponseEntity.ok().build();
        }

        String shopIdStr = parts[1]; 
        String orderIdStr = parts[2];
        UUID shopId = UUID.fromString(shopIdStr);
        UUID orderId = UUID.fromString(orderIdStr);

        CoffeeShop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new RuntimeException("Toko tidak ditemukan"));

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        // VERIFIKASI: Bandingkan token dari header dengan token yang disimpan di DB toko tersebut
        if (!shop.getXenditCallbackToken().equals(incomingToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String status = (String) data.get("status");
        if ("SUCCEEDED".equals(status) || "COMPLETED".equals(status) || "PAID".equals(status)) {
            order.setPaymentStatus("PAID");
            orderRepository.save(order);
        }

        return ResponseEntity.ok().build();
    }
}
