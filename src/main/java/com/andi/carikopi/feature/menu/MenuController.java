package com.andi.carikopi.feature.menu;

import com.andi.carikopi.feature.menu.dto.MenuFotoRequest;
import com.andi.carikopi.feature.menu.dto.MenuRequest;
import com.andi.carikopi.feature.menu.dto.MenuResponse;
import com.andi.carikopi.common.WebResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/{shopId}")
    public WebResponse<List<MenuResponse>> getMenusByShop(@PathVariable("shopId") UUID shopId) {
        return menuService.getMenusByShop(shopId);
    }

    @PostMapping(value = "/{shopId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebResponse<MenuResponse> create(@ModelAttribute @Valid @RequestBody MenuRequest request,
            @PathVariable("shopId") UUID shopId) {
        return menuService.create(request, shopId);
    }

    @PutMapping(value = "/{menuId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebResponse<MenuResponse> update(@ModelAttribute @Valid @RequestBody MenuRequest request,
            @PathVariable("menuId") UUID menuId) {
        return menuService.update(request, menuId);
    }

    @GetMapping("/{menuId}/detail")
    public WebResponse<MenuResponse> detail(@PathVariable("menuId") UUID menuId) {
        return menuService.getDetailMenu(menuId);
    }

    @PostMapping(value = "/{menuId}/upload-foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebResponse<String> uploadFoto(@ModelAttribute @Valid @RequestBody MenuFotoRequest request,
            @PathVariable("menuId") UUID menuId) {
        return menuService.uploadFotoMenu(request, menuId);
    }

    @DeleteMapping("/{menuId}/delete-foto/{fileId}")
    public WebResponse<String> deleteFoto(@PathVariable("menuId") UUID menuId,
            @PathVariable("fileId") UUID fileId) {
        return menuService.deleteFile(menuId, fileId);
    }

    @DeleteMapping("/{menuId}/delete")
    public WebResponse<String> delete(@PathVariable("menuId") UUID menuId) {
        return menuService.deleteMenu(menuId);
    }
}
