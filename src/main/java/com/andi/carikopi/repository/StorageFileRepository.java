package com.andi.carikopi.repository;

import com.andi.carikopi.entity.StorageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StorageFileRepository extends JpaRepository<StorageFile, UUID> {
}
