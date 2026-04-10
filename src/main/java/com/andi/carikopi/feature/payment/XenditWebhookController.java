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
        
        String externalId = (String) payload.get("external_id");
        // Kita simpan shopId di external_id saat create invoice tadi
        String shopIdStr = externalId.split("-")[1]; 
        String orderIdStr = externalId.split("-")[2];
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

        String status = (String) payload.get("status");
        if ("PAID".equals(status)) {
            order.setStatus("PAID");
            orderRepository.save(order);
        }

        return ResponseEntity.ok().build();
    }
}
