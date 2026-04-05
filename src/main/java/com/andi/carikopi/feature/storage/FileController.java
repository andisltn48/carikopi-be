package com.andi.carikopi.feature.storage;

import com.andi.carikopi.feature.storage.StorageFile;
import com.andi.carikopi.feature.storage.StorageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private StorageFileService storageFileService;

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable("fileId") UUID fileId) {
        StorageFile file = storageFileService.findById(fileId);

        Resource resource = storageFileService.load(file.getPath());
        String contentType = storageFileService.getContentType(resource);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
