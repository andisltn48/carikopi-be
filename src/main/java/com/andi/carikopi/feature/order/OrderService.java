package com.andi.carikopi.feature.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.andi.carikopi.common.PagingResponse;
import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopRepository;
import com.andi.carikopi.feature.menu.Menu;
import com.andi.carikopi.feature.menu.MenuRepository;
import com.andi.carikopi.feature.menu.dto.MenuResponse;
import com.andi.carikopi.feature.order.dto.OrderMenuRequest;
import com.andi.carikopi.feature.order.dto.OrderMenuResponse;
import com.andi.carikopi.feature.order.dto.OrderRequest;
import com.andi.carikopi.feature.order.dto.OrderResponse;
import com.andi.carikopi.feature.payment.PaymentService;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private CoffeeShopRepository coffeeShopRepository;
    @Autowired private MenuRepository menuRepository;
    @Autowired private RedisTemplate<String, String> redisTemplate;
    @Autowired private PaymentService paymentService;

    private static final String QUEUE_KEY_PREFIX = "queue:coffeeshop:";

    public String generateQueueNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = QUEUE_KEY_PREFIX + today;
        
        Long nextNumber = redisTemplate.opsForValue().increment(key);

        if (nextNumber == 1) {
            redisTemplate.expire(key, 48, TimeUnit.HOURS);
        }
        
        return String.format("%04d", nextNumber);
    }

    public WebResponse<OrderResponse> createOrder(OrderRequest request, Boolean isAdmin){
        
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
        order.setQueueNumber(generateQueueNumber());
        order.setOrderType(request.getOrderType());

        if (isAdmin) {
            order.setPaymentMethod(request.getPaymentMethod());
            order.setPaymentStatus("PAID");
            order.setStatus(request.getStatus());
        } else {
            order.setPaymentMethod("QRIS");
            order.setPaymentStatus("UNPAID");
            order.setStatus("PENDING");
        }

        List<OrderMenu> orderMenus = new ArrayList<>();
        for (OrderMenuRequest orderMenuRequest : request.getOrderMenus()) {
            Menu menu = menuRepository.findById(orderMenuRequest.getMenuId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

            OrderMenu orderMenu = new OrderMenu();
            orderMenu.setMenu(menu);
            orderMenu.setQuantity(orderMenuRequest.getQuantity());
            orderMenu.setTotalPrice(orderMenuRequest.getTotalPrice());
            orderMenu.setNotes(orderMenuRequest.getNotes());
            orderMenu.setOrder(order);

            orderMenus.add(orderMenu);
        }
        order.setOrderMenus(orderMenus);
        orderRepository.save(order);

        OrderResponse orderResponse = mapToOrderResponseSingle(order);
        if (!isAdmin) {
            // String paymentUrl = paymentService.createInvoice(shop, order);
            // orderResponse.setPaymentUrl(paymentUrl);

            Map<String, Object> qrCode = paymentService.createQRCode(shop, order);
            orderResponse.setQrString((String) qrCode.get("qr_string"));
            orderResponse.setQrId((String) qrCode.get("id"));
        }
        
        return WebResponse.<OrderResponse>builder()
                .status("OK")
                .code(200)
                .data(orderResponse)
                .build();
    }

    public WebResponse<List<OrderResponse>> getOrderByUniqueSessionAndShopId(String uniqueSession, UUID shopId){
        Specification<Order> spec = Specification.where(OrderSpecification.belongsToShop(shopId))
                .and(OrderSpecification.uniqueSessionContains(uniqueSession))
                .and(OrderSpecification.orderByCreatedAtAsc());
        List<Order> orders = orderRepository.findAll(spec);
        
        return mapToOrderResponse(orders);
    }

    public WebResponse<OrderResponse> getOrderByOrderNumber(String orderNumber){
        Specification<Order> spec = Specification.where(OrderSpecification.orderNumberContains(orderNumber))
                .and(OrderSpecification.orderByCreatedAtAsc());
        Order order = orderRepository.findOne(spec).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        OrderResponse orderResponse = mapToOrderResponseSingle(order);
        
        return WebResponse.<OrderResponse>builder()
                .status("OK")
                .code(200)
                .data(orderResponse)
                .build();
    }

    public WebResponse<OrderResponse> getOrderByOrderId(UUID orderId){
        Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        OrderResponse orderResponse = mapToOrderResponseSingle(order);
        
        return WebResponse.<OrderResponse>builder()
                .status("OK")
                .code(200)
                .data(orderResponse)
                .build();
    }

    private String generateOrderNumber() {
        String prefix = "ORD-";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp;
    }

    private WebResponse<List<OrderResponse>> mapToOrderResponse(List<Order> orders) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            OrderResponse orderResponse = mapToOrderResponseSingle(order);
            orderResponses.add(orderResponse);
        }
        return WebResponse.<List<OrderResponse>>builder()
                .status("OK")
                .code(200)
                .data(orderResponses)
                .build();
    }

    private OrderResponse mapToOrderResponseSingle(Order order) {
        OrderResponse orderResponse = OrderResponse.builder()
        .name(order.getName())
        .phone(order.getPhone())
        .totalPrice(order.getTotalPrice())
        .orderMenus(order.getOrderMenus().stream().map(orderMenu -> {
            MenuResponse menuResponse = MenuResponse.builder()
            .id(orderMenu.getMenu().getId())
            .nama(orderMenu.getMenu().getNama())
            .harga(orderMenu.getMenu().getHarga())
            .deskripsi(orderMenu.getMenu().getDeskripsi())
            .build();

            return OrderMenuResponse.builder()
                    .menu(menuResponse)
                    .quantity(orderMenu.getQuantity())
                    .totalPrice(orderMenu.getTotalPrice())
                    .notes(orderMenu.getNotes())
                    .build();
        }).collect(Collectors.toList()))
        .status(order.getStatus())
        .paymentStatus(order.getPaymentStatus())
        .paymentMethod(order.getPaymentMethod())
        .orderNumber(order.getOrderNumber())
        .createdAt(order.getCreatedAt())
        .queueNumber(order.getQueueNumber())
        .orderType(order.getOrderType())
        .id(order.getId())
        .build();

        return orderResponse;
    }

    public WebResponse<List<OrderResponse>> getOrderByShopId(UUID shopId, String orderNumber, String status, Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Order> spec = Specification.where(OrderSpecification.belongsToShop(shopId))
                .and(OrderSpecification.orderByCreatedAtAsc());

        if (orderNumber != null && !orderNumber.isBlank()) {
            spec = spec.and(OrderSpecification.orderNumberContains(orderNumber));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }

        Page<Order> orders = orderRepository.findAll(spec, pageable);
        
        List<OrderResponse> orderResponses = orders.getContent().stream()
                .map(this::mapToOrderResponseSingle)
                .collect(Collectors.toList());

        return WebResponse.<List<OrderResponse>>builder()
                .status("OK")
                .code(200)
                .data(orderResponses)
                .paging(PagingResponse.builder()
                        .currentPage(orders.getNumber())
                        .totalPage(orders.getTotalPages())
                        .size(orders.getSize())
                        .build())
                .build();
    }

    public WebResponse<String> updateOrderStatus(UUID orderId, String status){
        Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        order.setStatus(status);
        orderRepository.save(order);
        
        return WebResponse.<String>builder()
                .status("OK")
                .code(200)
                .data("Order status updated successfully")
                .build();
    }
        
}
