package com.andi.carikopi.feature.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;

@Data
public class MenuRequest {
    @NotBlank(message = "nama tidak boleh ksoong")
    @NotNull(message = "nama harus diisi")
    private String nama;

    private BigInteger harga;

    private List<MultipartFile> foto;

    private String deskripsi;

    private String category;

    @JsonProperty("is_favorite")
    private Boolean isFavorite;
}
