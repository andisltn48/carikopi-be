package com.andi.carikopi.feature.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.order.dto.OrderRequest;

@RestController
@RequestMapping("/api/public/orders")
public class OrderPublicController {
    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<Order> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping(path = "/get-by-unique-session/{uniqueSession}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<Order>> getOrderByUniqueSession(@PathVariable("uniqueSession") String uniqueSession) {
        return orderService.getOrderByUniqueSession(uniqueSession);
    }

    @GetMapping(path = "/get-by-order-number/{orderNumber}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<Order> getOrderByOrderNumber(@PathVariable("orderNumber") String orderNumber) {
        return orderService.getOrderByOrderNumber(orderNumber);
    }
}
