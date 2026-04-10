package com.andi.carikopi.feature.order;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.order.dto.OrderRequest;
import com.andi.carikopi.feature.order.dto.OrderResponse;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping(path = "/get-by-shop-id/{shopId}")
    public WebResponse<List<OrderResponse>> getOrderByShopId(
            @PathVariable("shopId") UUID shopId,
            @RequestParam(value = "order_number", required = false) String orderNumber,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return orderService.getOrderByShopId(shopId, orderNumber, status, page, size);
    }

    @GetMapping(path = "/get-by-order-number/{orderNumber}")
    public WebResponse<OrderResponse> getOrderByOrderNumber(@PathVariable("orderNumber") String orderNumber) {
        return orderService.getOrderByOrderNumber(orderNumber);
    }

    @GetMapping(path = "/get-by-order-id/{orderId}")
    public WebResponse<OrderResponse> getOrderByOrderId(@PathVariable("orderId") UUID orderId) {
        return orderService.getOrderByOrderId(orderId);
    }

    @GetMapping(path = "/update-status/{orderId}/{status}")
    public WebResponse<String> updateOrderStatus(@PathVariable("orderId") UUID orderId, @PathVariable("status") String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request, true);
    }
}
