package com.andi.carikopi.feature.coffeeshop;

import com.andi.carikopi.feature.coffeeshop.dto.CoffeeShopRequest;
import com.andi.carikopi.feature.coffeeshop.dto.CoffeeShopResponse;
import com.andi.carikopi.feature.coffeeshop.dto.XenditApiRequest;
import com.andi.carikopi.feature.coffeeshop.dto.XenditApiResponse;
import com.andi.carikopi.common.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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

    @PostMapping("/foto-profil/upload/{shopId}")
    public WebResponse<String> uploadFotoProfil(@RequestParam("file") MultipartFile file, @PathVariable("shopId") UUID shopId) {
        return coffeeShopService.uploadFotoProfil(file, shopId);
    }

    @DeleteMapping("/foto-profil/delete/{shopId}")
    public WebResponse<String> deleteFotoProfil(@PathVariable("shopId") UUID shopId) {
        return coffeeShopService.deleteFotoProfil(shopId);
    }

    @PostMapping("/xendit-api/update/{shopId}")
    public WebResponse<XenditApiResponse> updateXenditApi(@PathVariable("shopId") UUID shopId, @RequestBody XenditApiRequest request) {
        return coffeeShopService.updateXenditApi(shopId, request);
    }

    @GetMapping("/xendit-api/{shopId}")
    public WebResponse<XenditApiResponse> getXenditApi(@PathVariable("shopId") UUID shopId) {
        return coffeeShopService.getXenditApi(shopId);
    }
}
