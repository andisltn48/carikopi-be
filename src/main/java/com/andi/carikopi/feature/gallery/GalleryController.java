package com.andi.carikopi.feature.gallery;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.gallery.dto.GalleryRequest;
import com.andi.carikopi.feature.gallery.dto.GalleryResponse;

@RestController
@RequestMapping("/api/galleries")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("/{shopId}")
    public WebResponse<List<GalleryResponse>> getGalleries(@PathVariable UUID shopId) {
        return galleryService.getGalleries(shopId);
    }

    @PostMapping(path = "/{shopId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebResponse<String> create(@PathVariable UUID shopId, @ModelAttribute GalleryRequest request) {
        return galleryService.create(request, shopId);
    }

    @DeleteMapping("/{galleryId}/{shopId}")
    public WebResponse<String> delete(@PathVariable UUID galleryId, @PathVariable UUID shopId) {
        return galleryService.delete(galleryId, shopId);
    }
}
