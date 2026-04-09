package com.andi.carikopi.feature.gallery.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GalleryRequest {
    @NotBlank(message = "nama tidak boleh kosong")
    @NotNull(message = "nama harus diisi")
    private String nama;

    @NotNull(message = "foto harus diisi")
    private MultipartFile foto;
}
