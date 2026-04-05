package com.andi.carikopi.feature.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StorageFileRepository extends JpaRepository<StorageFile, UUID> {
}
