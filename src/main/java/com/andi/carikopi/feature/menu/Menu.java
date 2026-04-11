package com.andi.carikopi.feature.menu;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.andi.carikopi.feature.coffeeshop.CoffeeShop;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "menus")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nama;

    private BigInteger harga;

    @ElementCollection
    @CollectionTable(name = "menu_photos", joinColumns = @JoinColumn(name = "menu_id"))
    @Column(name = "storage_file_id")
    private List<UUID> foto = new ArrayList<>();

    private String deskripsi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private CoffeeShop shop;

    private String category;

    private Boolean isFavorite;
    
    @Column(name = "active")
    private Boolean active = true;

}
