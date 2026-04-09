package com.andi.carikopi.feature.gallery;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.gallery.dto.GalleryResponse;

@RestController
@RequestMapping("/api/public/galleries")
public class GalleryPublicController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("/{shopId}")
    public WebResponse<List<GalleryResponse>> getGalleries(@PathVariable UUID shopId) {
        return galleryService.getGalleries(shopId);
    }
}
