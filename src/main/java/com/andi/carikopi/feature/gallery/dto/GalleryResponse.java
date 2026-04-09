package com.andi.carikopi.feature.gallery.dto;

import java.util.UUID;

import com.andi.carikopi.feature.storage.dto.StorageFileResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GalleryResponse {
    private UUID id;
    private String nama;
    private StorageFileResponse foto;
}
