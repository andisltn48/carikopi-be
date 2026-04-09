package com.andi.carikopi.feature.coffeeshop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.andi.carikopi.feature.auth.User;
import com.andi.carikopi.feature.gallery.Gallery;
import com.andi.carikopi.feature.menu.Menu;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coffeeshops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeShop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true)
    private String namaToko;

    @Column(nullable = true)
    private String alamat;

    @Column(nullable = true)
    private String deskripsi;

    @Column(nullable = true)
    private String tags;

    @Column(nullable = true)
    private UUID fotoProfil;

    @JsonIgnore
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Menu> menus;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String instagram;

    @Column(nullable = true)
    private String tiktok;

    @Column(nullable = true)
    private String whatsapp;

    @Column(nullable = true)
    private String facebook;

    @Column(nullable = true)
    private String twitter;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Gallery> galleries;
}
