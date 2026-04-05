package com.andi.carikopi.feature.menu;

import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findMenuByShop(CoffeeShop shop);
}
