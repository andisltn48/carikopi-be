package com.andi.carikopi.feature.menu;

import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.storage.StorageFileService;
import com.andi.carikopi.feature.menu.dto.*;
import com.andi.carikopi.feature.storage.dto.StorageFileResponse;
import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.storage.StorageFile;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MenuService {

        @Autowired
        private MenuRepository menuRepository;
        @Autowired
        private CoffeeShopRepository coffeeShopRepository;
        @Autowired
        private StorageFileService storageFileService;

        @Value("${app.url}")
        private String appUrl;

        public WebResponse<List<MenuResponse>> getMenusByShop(UUID shopId, String category) {
                CoffeeShop shop = coffeeShopRepository.findById(shopId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Coffeeshp not found!"));

                List<MenuResponse> favMenus = new ArrayList<>();
                List<MenuResponse> normalMenus = new ArrayList<>();

                List<Menu> menus = menuRepository.findByShopAndActiveTrue(shop);
                if (category != null) {
                        menus = menuRepository.findByShopAndCategoryAndActiveTrue(shop, category);
                }

                for (Menu menu : menus) {
                        List<StorageFileResponse> files = new ArrayList<>();
                        for (UUID id : menu.getFoto()) {
                                StorageFile file = storageFileService.findById(id);
                                String fullUrl = appUrl + "api/files/" + file.getId();
                                StorageFileResponse storageFileResponse = StorageFileResponse.builder()
                                                .id(file.getId())
                                                .filename(file.getFilename())
                                                .url(fullUrl)
                                                .build();
                                files.add(storageFileResponse);
                        }
                        MenuResponse menuResponse = MenuResponse.builder()
                                        .id(menu.getId())
                                        .nama(menu.getNama())
                                        .foto(files)
                                        .deskripsi(menu.getDeskripsi())
                                        .harga(menu.getHarga())
                                        .isFavorite(menu.getIsFavorite())
                                        .category(menu.getCategory())
                                        .build();
                        if (menu.getIsFavorite() != null && menu.getIsFavorite()) {
                                favMenus.add(menuResponse);
                        } else {
                                normalMenus.add(menuResponse);
                        }
                }

                List<MenuResponse> result = new ArrayList<>();
                result.addAll(favMenus);
                result.addAll(normalMenus);

                return WebResponse.<List<MenuResponse>>builder()
                                .code(200)
                                .status("OK")
                                .data(result)
                                .errors(null)
                                .build();
        }

        public WebResponse<MenuResponse> create(MenuRequest request, UUID shopId) {
                CoffeeShop shop = coffeeShopRepository.findById(shopId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Coffeeshp not found!"));

                Menu menu = new Menu();
                menu.setNama(request.getNama());
                menu.setDeskripsi(request.getDeskripsi());
                menu.setHarga(request.getHarga());
                menu.setCategory(request.getCategory());
                menu.setIsFavorite(request.getIsFavorite());
                menu.setShop(shop);

                // handle upload foto
                if (!request.getFoto().isEmpty()) {
                        List<UUID> fileIds = storageFileService.uploadMultipleFiles(request.getFoto(), "menu_foto");

                        menu.setFoto(fileIds);
                }

                menuRepository.save(menu);

                return getMenuResponseWeb(menu);
        }

        public WebResponse<MenuResponse> update(MenuRequest request, UUID menuId) {
                Menu menu = menuRepository.findById(menuId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Menu tidak ditemukan!"));

                menu.setNama(request.getNama());
                menu.setHarga(request.getHarga());
                menu.setDeskripsi(request.getDeskripsi());
                menu.setCategory(request.getCategory());
                menu.setIsFavorite(request.getIsFavorite());

                menuRepository.save(menu);

                return getMenuResponseWeb(menu);
        }

        public WebResponse<MenuResponse> getDetailMenu(UUID menuId) {
                Menu menu = menuRepository.findById(menuId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Menu tidak ditemukan!"));

                return getMenuResponseWeb(menu);
        }

        private WebResponse<MenuResponse> getMenuResponseWeb(Menu menu) {
                List<StorageFileResponse> files = new ArrayList<>();
                for (UUID id : menu.getFoto()) {
                        StorageFile file = storageFileService.findById(id);
                        String fullUrl = appUrl + "api/files/" + file.getId();
                        StorageFileResponse storageFileResponse = StorageFileResponse.builder()
                                        .id(file.getId())
                                        .filename(file.getFilename())
                                        .url(fullUrl)
                                        .build();
                        files.add(storageFileResponse);
                }
                MenuResponse menuResponse = MenuResponse.builder()
                                .id(menu.getId())
                                .nama(menu.getNama())
                                .foto(files)
                                .deskripsi(menu.getDeskripsi())
                                .harga(menu.getHarga())
                                .deskripsi(menu.getDeskripsi())
                                .build();

                return WebResponse.<MenuResponse>builder()
                                .code(200)
                                .status("OK")
                                .data(menuResponse)
                                .errors(null)
                                .build();
        }

        public WebResponse<String> uploadFotoMenu(MenuFotoRequest request, UUID menuId) {
                Menu menu = menuRepository.findById(menuId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Menu not found!"));

                List<UUID> currentFotos = menu.getFoto();
                if (!request.getFoto().isEmpty()) {
                        List<UUID> fileIds = storageFileService.uploadMultipleFiles(request.getFoto(), "menu_foto");

                        currentFotos.addAll(fileIds);
                }

                menu.setFoto(currentFotos);
                menuRepository.save(menu);

                return WebResponse.<String>builder()
                                .code(200)
                                .status("OK")
                                .data("Upload foto berhasil")
                                .errors(null)
                                .build();
        }

        public WebResponse<String> deleteFile(UUID menuId, UUID fileId) {
                Menu menu = menuRepository.findById(menuId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Menu not found!"));

                List<UUID> currentFotos = menu.getFoto();
                if (!currentFotos.contains(fileId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto tidak ditemukan!");
                }

                currentFotos.remove(fileId);
                menu.setFoto(currentFotos);
                menuRepository.save(menu);

                storageFileService.deleteFile(fileId);

                return WebResponse.<String>builder()
                                .code(200)
                                .status("OK")
                                .data("Delete foto berhasil")
                                .errors(null)
                                .build();
        }

        public WebResponse<String> deleteMenu(UUID menuId) {
                Menu menu = menuRepository.findById(menuId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Menu not found!"));

                menu.setActive(false);
                menuRepository.save(menu);

                return WebResponse.<String>builder()
                                .code(200)
                                .status("OK")
                                .data("Delete menu berhasil")
                                .errors(null)
                                .build();
        }
}
