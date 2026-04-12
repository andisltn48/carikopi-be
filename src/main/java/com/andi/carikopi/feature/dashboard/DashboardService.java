package com.andi.carikopi.feature.dashboard;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopRepository;
import com.andi.carikopi.feature.dashboard.dto.DashboardResponse;
import com.andi.carikopi.feature.menu.MenuRepository;
import com.andi.carikopi.feature.order.Order;
import com.andi.carikopi.feature.order.OrderMenuRepository;
import com.andi.carikopi.feature.order.OrderRepository;
import com.andi.carikopi.feature.order.OrderService;
import com.andi.carikopi.feature.order.OrderSpecification;

@Service
public class DashboardService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderMenuRepository orderMenuRepository;
    @Autowired private OrderService orderService;
    @Autowired private CoffeeShopRepository coffeeShopRepository;
    @Autowired private MenuRepository menuRepository;

    public WebResponse<DashboardResponse> getDashboard(UUID shopId, String startDate, String endDate) {
        
        DashboardResponse response = new DashboardResponse();

        List<Order> ordersToday = orderRepository.findAll(Specification.where(OrderSpecification.belongsToShop(shopId))
                .and(OrderSpecification.orderCompleted())
                .and(OrderSpecification.orderCreatedToday()));
        
        double totalPenjualanHariIni = ordersToday.stream().mapToDouble(Order::getTotalPrice).sum();
        int totalTransaksiHariIni = ordersToday.size();

        List<Order> ordersMonth = orderRepository.findAll(Specification.where(OrderSpecification.belongsToShop(shopId))
                .and(OrderSpecification.orderCompleted())
                .and(OrderSpecification.orderCreatedThisMonth()));
        
        double totalPendapatanBulanIni = ordersMonth.stream().mapToDouble(Order::getTotalPrice).sum();
        
        // Populate Response
        Map<String, Object> penjualanHariIni = new HashMap<>();
        penjualanHariIni.put("value", totalPenjualanHariIni);
        
        Map<String, Object> jumlahTransaksiHariIni = new HashMap<>();
        jumlahTransaksiHariIni.put("value", totalTransaksiHariIni);
        
        Map<String, Object> pendapatanBulanIni = new HashMap<>();
        pendapatanBulanIni.put("value", totalPendapatanBulanIni);
        
        // Ambil Top 5 Menu Terlaris
        List<Map<String, Object>> listMenuTerlaris = orderMenuRepository.findTopSellingMenus(shopId, PageRequest.of(0, 5));
        
        Map<String, Object> menuTerlaris = new HashMap<>();
        if (!listMenuTerlaris.isEmpty()) {
            menuTerlaris = listMenuTerlaris.get(0);
        }
        
        response.setPenjualanHariIni(penjualanHariIni);
        response.setJumlahTransaksiHariIni(jumlahTransaksiHariIni);
        response.setPendapatanBulanIni(pendapatanBulanIni);
        response.setMenuTerlaris(menuTerlaris);
        response.setListMenuTerlaris(listMenuTerlaris);
        response.setGrafikPenjualan(orderService.getGrafikPenjualan(shopId));
        
        return WebResponse.<DashboardResponse>builder()
                .status("OK")
                .code(200)
                .data(response)
                .build();
    }
}
