package com.andi.carikopi.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoffeeShopRequest {

    @JsonProperty("nama_toko")
    private String namaToko;
    @JsonProperty("foto_profil")
    private MultipartFile fotoProfil;

    private String alamat;
    private String deskripsi;
    private String tags;
    private Double latitude;
    private Double longitude;
    private String city;

    private String instagram;
    private String tiktok;
    private String whatsapp;
    private String facebook;
    private String twitter;
}
