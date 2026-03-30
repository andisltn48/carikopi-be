package com.andi.carikopi.controller;

import com.andi.carikopi.dto.CoffeeShopRequest;
import com.andi.carikopi.dto.CoffeeShopResponse;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.service.CoffeeShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/coffeeshop")
public class CoffeeShopController {

    @Autowired private CoffeeShopService coffeeShopService;

    @PostMapping("/submit")
    public WebResponse<CoffeeShopResponse> create(@RequestBody CoffeeShopRequest request, Principal principal) {
        return coffeeShopService.createProfile(request, principal.getName());
    }

    @GetMapping("/mine")
    public WebResponse<CoffeeShopResponse> getMine(Principal principal) {
        return coffeeShopService.getMyCoffeeShop(principal.getName());
    }


}
