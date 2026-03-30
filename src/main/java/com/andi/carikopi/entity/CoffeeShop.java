package com.andi.carikopi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

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

    @Column(nullable = false)
    private String namaToko;

    private String alamat;
    private String deskripsi;
    private String tags;
    private UUID fotoProfil;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Menu> menus;
}
