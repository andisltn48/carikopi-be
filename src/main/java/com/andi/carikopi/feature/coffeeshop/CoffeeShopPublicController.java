package com.andi.carikopi.feature.coffeeshop;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andi.carikopi.feature.coffeeshop.dto.CoffeeShopResponse;
import com.andi.carikopi.feature.menu.dto.MenuResponse;
import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopService;
import com.andi.carikopi.feature.menu.MenuService;

@RestController
@RequestMapping("/api/public/coffeeshop")
public class CoffeeShopPublicController {

    @Autowired private CoffeeShopService coffeeShopService;

    @Autowired private MenuService menuService;

    @GetMapping("/{shopId}/detail-shop")
    public WebResponse<CoffeeShopResponse> getDetailCoffeeShopPublic(@PathVariable("shopId") UUID shopId) {
        return coffeeShopService.getDetailCoffeeShopPublic(shopId);
    }

    @GetMapping("/{shopId}/menu")
    public WebResponse<List<MenuResponse>> getMenusByShop(@PathVariable("shopId") UUID shopId) {
        return menuService.getMenusByShop(shopId);
    }

    @GetMapping("/nearby")
    public WebResponse<List<CoffeeShopResponse>> getNearby(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(value = "radius", defaultValue = "5") double radiusKm,
            @RequestParam(value = "query", required = false) String query
    ) {
        return coffeeShopService.find(lat, lng, radiusKm, query);
    }
}
