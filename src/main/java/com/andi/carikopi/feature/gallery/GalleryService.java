package com.andi.carikopi.feature.gallery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopRepository;
import com.andi.carikopi.feature.gallery.dto.GalleryRequest;
import com.andi.carikopi.feature.gallery.dto.GalleryResponse;
import com.andi.carikopi.feature.storage.StorageFile;
import com.andi.carikopi.feature.storage.StorageFileService;
import com.andi.carikopi.feature.storage.dto.StorageFileResponse;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private StorageFileService storageFileService;

    @Autowired
    private CoffeeShopRepository coffeeShopRepository;

    @Value("${app.url}")
    private String appUrl;

    public WebResponse<List<GalleryResponse>> getGalleries(UUID shopId) {
        List<Gallery> galleries = galleryRepository.findAllByShopId(shopId);
        List<GalleryResponse> responses = new ArrayList<>();

        for (Gallery gallery : galleries) {
            StorageFile file = storageFileService.findById(gallery.getFoto());
            String fullUrl = appUrl + "api/files/" + file.getId();
            StorageFileResponse storageFileResponse = StorageFileResponse.builder()
                    .id(file.getId())
                    .filename(file.getFilename())
                    .url(fullUrl)
                    .build();

            GalleryResponse response = GalleryResponse.builder()
                    .id(gallery.getId())
                    .foto(storageFileResponse)
                    .nama(gallery.getNama())
                    .build();

            responses.add(response);
        }

        return WebResponse.<List<GalleryResponse>>builder()
                .data(responses)
                .code(200)
                .status("OK")
                .build();
    }

    public WebResponse<String> create(GalleryRequest request, UUID shopId) {
        CoffeeShop shop = coffeeShopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffeeshop not found!"));
        UUID idFoto = storageFileService.uploadFile(request.getFoto(), "galeri");

        Gallery gallery = new Gallery();
        gallery.setNama(request.getNama());
        gallery.setFoto(idFoto);
        gallery.setShop(shop);

        galleryRepository.save(gallery);

        return WebResponse.<String>builder()
                .data("Gallery berhasil ditambahkan!")
                .code(200)
                .status("OK")
                .build();
    }
    
    public WebResponse<String> delete(UUID galleryId, UUID shopId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gallery not found!"));
        
        if (!gallery.getShop().getId().equals(shopId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this gallery!");
        }
        
        galleryRepository.delete(gallery);
        
        return WebResponse.<String>builder()
                .data("Gallery berhasil dihapus!")
                .code(200)
                .status("OK")
                .build();
    }
}
