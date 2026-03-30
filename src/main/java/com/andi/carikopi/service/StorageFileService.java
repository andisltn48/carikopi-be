package com.andi.carikopi.service;

import com.andi.carikopi.entity.StorageFile;
import com.andi.carikopi.repository.StorageFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StorageFileService {

    @Autowired
    private StorageFileRepository storageFileRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public UUID uploadFile(MultipartFile file, String folder) {
        try {
            if (file.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Kosong");

            Path path = Paths.get(uploadDir + folder);
            if (!Files.exists(path))
                Files.createDirectories(path);

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = folder + "_" + UUID.randomUUID() + "." + extension;

            Path targetPath = path.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            StorageFile storageFile = new StorageFile();
            storageFile.setFilename(fileName);
            storageFile.setPath(folder + "/" + fileName);

            storageFileRepository.save(storageFile);

            return storageFile.getId();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gagal menyimpan file ke server");
        }
    }

    public List<UUID> uploadMultipleFiles(List<MultipartFile> files, String folder) {
        List<UUID> fileIds = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {

                UUID id = uploadFile(file, folder);
                fileIds.add(id);
            }
        }

        return fileIds;
    }

    public StorageFile findById(UUID fileId) {
        return storageFileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }

    public Resource load(String path) {
        try {
            Path file = Paths.get(uploadDir).resolve(path);
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public String getContentType(Resource resource) {
        try {
            String contentType = Files.probeContentType(Paths.get(resource.getURI()));
            return (contentType != null) ? contentType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
