package com.andi.carikopi.controller;

import com.andi.carikopi.dto.CoffeeShopRequest;
import com.andi.carikopi.dto.CoffeeShopResponse;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.service.CoffeeShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coffeeshop")
public class CoffeeShopController {

    @Autowired private CoffeeShopService coffeeShopService;

    @PostMapping("/submit")
    public WebResponse<CoffeeShopResponse> create(
            @RequestBody CoffeeShopRequest request,
            @RequestParam(value = "shopId", required = false) UUID shopId,
            Principal principal) {
        return coffeeShopService.createProfile(request, principal.getName(), shopId);
    }

    @GetMapping("/mine")
    public WebResponse<CoffeeShopResponse> getMine(Principal principal) {
        return coffeeShopService.getMyCoffeeShop(principal.getName());
    }

    @GetMapping("/nearby")
    public WebResponse<List<CoffeeShopResponse>> getNearby(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(value = "radius", defaultValue = "5") double radiusKm
    ) {
        return coffeeShopService.findNearby(lat, lng, radiusKm);
    }

    @PostMapping("/foto-profil/upload/{shopId}")
    public WebResponse<String> uploadFotoProfil(@RequestParam("file") MultipartFile file, @PathVariable("shopId") UUID shopId) {
        return coffeeShopService.uploadFotoProfil(file, shopId);
    }

    @DeleteMapping("/foto-profil/delete/{shopId}")
    public WebResponse<String> deleteFotoProfil(@PathVariable("shopId") UUID shopId) {
        return coffeeShopService.deleteFotoProfil(shopId);
    }
}
