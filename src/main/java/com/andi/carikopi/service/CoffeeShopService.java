package com.andi.carikopi.service;

import com.andi.carikopi.dto.CoffeeShopRequest;
import com.andi.carikopi.dto.CoffeeShopResponse;
import com.andi.carikopi.dto.UserResponse;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.entity.CoffeeShop;
import com.andi.carikopi.entity.User;
import com.andi.carikopi.repository.CoffeeShopRepository;
import com.andi.carikopi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CoffeeShopService {
    @Autowired private CoffeeShopRepository coffeeShopRepository;
    @Autowired private UserRepository userRepository;

    public WebResponse<CoffeeShopResponse> createProfile(CoffeeShopRequest request, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CoffeeShop coffeeShop = new CoffeeShop();
        coffeeShop.setNamaToko(request.getNamaToko());
        coffeeShop.setAlamat(request.getAlamat());
        coffeeShop.setDeskripsi(request.getDeskripsi());
        coffeeShop.setUser(user);
        coffeeShop.setTags(request.getTags());

        coffeeShopRepository.save(coffeeShop);

        return getCoffeeShopResponseWebResponse(user, coffeeShop);
    }

        public WebResponse<CoffeeShopResponse> getMyCoffeeShop(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));

        CoffeeShop coffeeShop = coffeeShopRepository.findCoffeeShopByUser(user);

        return getCoffeeShopResponseWebResponse(user, coffeeShop);
    }

    private WebResponse<CoffeeShopResponse> getCoffeeShopResponseWebResponse(User user, CoffeeShop coffeeShop) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        CoffeeShopResponse coffeeShopResponse = CoffeeShopResponse.builder()
                .id(coffeeShop.getId())
                .namaToko(coffeeShop.getNamaToko())
                .alamat(coffeeShop.getAlamat())
                .deskripsi(coffeeShop.getDeskripsi())
                .user(userResponse)
                .tags(coffeeShop.getTags())
                .build();

        return WebResponse.<CoffeeShopResponse>builder()
                .code(200)
                .status("OK")
                .data(coffeeShopResponse)
                .build();
    }
}
