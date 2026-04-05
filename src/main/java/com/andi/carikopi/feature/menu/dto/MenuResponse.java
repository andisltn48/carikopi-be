package com.andi.carikopi.feature.menu.dto;

import lombok.Builder;
import lombok.Data;
import com.andi.carikopi.feature.storage.dto.StorageFileResponse;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MenuResponse {
    private UUID id;

    private String nama;

    private BigInteger harga;

    private List<StorageFileResponse> foto;

    private String deskripsi;
}
