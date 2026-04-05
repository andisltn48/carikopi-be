package com.andi.carikopi.feature.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopRepository;
import com.andi.carikopi.feature.menu.Menu;
import com.andi.carikopi.feature.menu.MenuRepository;
import com.andi.carikopi.feature.order.dto.OrderMenuRequest;
import com.andi.carikopi.feature.order.dto.OrderRequest;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderMenuRepository orderMenuRepository;
    @Autowired private CoffeeShopRepository coffeeShopRepository;
    @Autowired private MenuRepository menuRepository;

    public WebResponse<Order> createOrder(OrderRequest request){
        
        CoffeeShop shop = coffeeShopRepository.findById(request.getShopId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee shop not found"));

        Order order = new Order();
        order.setName(request.getName());
        order.setPhone(request.getPhone());
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus("PENDING");
        order.setCreatedAt(new Date());
        order.setShop(shop);
        order.setOrderNumber(generateOrderNumber());
        order.setUniqueSession(request.getUniqueSession());

        List<OrderMenu> orderMenus = new ArrayList<>();
        for (OrderMenuRequest orderMenuRequest : request.getOrderMenus()) {
            Menu menu = menuRepository.findById(orderMenuRequest.getMenuId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

            OrderMenu orderMenu = new OrderMenu();
            orderMenu.setMenu(menu);
            orderMenu.setQuantity(orderMenuRequest.getQuantity());
            orderMenu.setTotalPrice(orderMenuRequest.getTotalPrice());
            orderMenuRepository.save(orderMenu);

            orderMenus.add(orderMenu);
        }
        order.setOrderMenus(orderMenus);
        orderRepository.save(order);
        return WebResponse.<Order>builder()
                .status("OK")
                .code(200)
                .data(order)
                .build();
    }

    public WebResponse<List<Order>> getOrderByUniqueSession(String uniqueSession){
        List<Order> order = orderRepository.findAllByUniqueSession(uniqueSession);
        return WebResponse.<List<Order>>builder()
                .status("OK")
                .code(200)
                .data(order)
                .build();
    }

    public WebResponse<Order> getOrderByOrderNumber(String orderNumber){
        Order order = orderRepository.findByOrderNumber(orderNumber)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return WebResponse.<Order>builder()
                .status("OK")
                .code(200)
                .data(order)
                .build();
    }

    private String generateOrderNumber() {
        String prefix = "ORD-";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp;
    }
}
