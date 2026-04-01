package com.andi.carikopi.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andi.carikopi.dto.CoffeeShopResponse;
import com.andi.carikopi.dto.MenuResponse;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.service.CoffeeShopService;
import com.andi.carikopi.service.MenuService;

@RestController
@RequestMapping("/api/public/coffeeshop")
public class CoffeeShopPublicController {

    @Autowired private CoffeeShopService coffeeShopService;

    @Autowired private MenuService menuService;

    @GetMapping("/{shopId}")
    public WebResponse<CoffeeShopResponse> getDetailCoffeeShopPublic(@PathVariable("shopId") UUID shopId) {
        return coffeeShopService.getDetailCoffeeShopPublic(shopId);
    }

    @GetMapping("/{shopId}/menu")
    public WebResponse<List<MenuResponse>> getMenusByShop(@PathVariable("shopId") UUID shopId) {
        return menuService.getMenusByShop(shopId);
    }
}
