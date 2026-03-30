package com.andi.carikopi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoffeeShopRequest {

    @JsonProperty("nama_toko")
    private String namaToko;

    private String alamat;
    private String deskripsi;
    private String tags;
}
