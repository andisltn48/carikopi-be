package com.andi.carikopi.feature.coffeeshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.andi.carikopi.feature.auth.dto.UserResponse;
import com.andi.carikopi.feature.storage.dto.StorageFileResponse;
import com.andi.carikopi.feature.menu.dto.MenuResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoffeeShopResponse {
    private UUID id;
    @JsonProperty("nama_toko")
    private String namaToko;
    private String alamat;
    private String deskripsi;
    private String tags;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private UserResponse user;
    private StorageFileResponse fotoProfil;
    private MenuResponse menu;
    private String city;

    private String instagram;
    private String tiktok;
    private String whatsapp;
    private String facebook;
    private String twitter;
}
