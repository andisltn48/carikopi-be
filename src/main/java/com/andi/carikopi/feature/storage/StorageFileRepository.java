package com.andi.carikopi.feature.storage;

import com.andi.carikopi.feature.storage.StorageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StorageFileRepository extends JpaRepository<StorageFile, UUID> {
}
