package com.andi.carikopi.feature.gallery;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryRepository extends JpaRepository<Gallery, UUID> {
    List<Gallery> findAllByShopId(UUID shopId);
}
