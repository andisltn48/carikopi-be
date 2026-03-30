package com.andi.carikopi.repository;

import com.andi.carikopi.entity.CoffeeShop;
import com.andi.carikopi.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findMenuByShop(CoffeeShop shop);
}
