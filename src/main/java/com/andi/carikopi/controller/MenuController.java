package com.andi.carikopi.controller;

import com.andi.carikopi.dto.MenuRequest;
import com.andi.carikopi.dto.MenuResponse;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/{shopId}")
    public WebResponse<List<MenuResponse>> getMenusByShop(@PathVariable("shopId") UUID shopId) {
        return menuService.getMenusByShop(shopId);
    }

    @PostMapping(value = "/{shopId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebResponse<MenuResponse> create(@ModelAttribute @Valid @RequestBody MenuRequest request,
            @PathVariable("shopId") UUID shopId) {
        return menuService.create(request, shopId);
    }

    @PutMapping(value = "/{menuId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebResponse<MenuResponse> update(@ModelAttribute @Valid @RequestBody MenuRequest request,
            @PathVariable("menuId") UUID menuId) {
        return menuService.update(request, menuId);
    }

    @GetMapping("/{menuId}/detail")
    public WebResponse<MenuResponse> detail(@PathVariable("menuId") UUID menuId) {
        return menuService.getDetailMenu(menuId);
    }
}
