package com.andi.carikopi.feature.storage.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StorageFileResponse {
    private UUID id;

    private String filename;

    private String url;
}
