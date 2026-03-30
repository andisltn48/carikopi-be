package com.andi.carikopi.repository;

import com.andi.carikopi.entity.CoffeeShop;
import com.andi.carikopi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoffeeShopRepository extends JpaRepository<CoffeeShop, UUID> {
    CoffeeShop findCoffeeShopByUser(User user);
}
