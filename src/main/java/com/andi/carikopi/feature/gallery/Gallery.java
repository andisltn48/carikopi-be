package com.andi.carikopi.feature.gallery;

import java.util.UUID;

import com.andi.carikopi.feature.coffeeshop.CoffeeShop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "galleries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = true)
    private String nama;
    @Column(nullable = false)
    private UUID foto;
    
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false, referencedColumnName = "id")
    private CoffeeShop shop;
}
