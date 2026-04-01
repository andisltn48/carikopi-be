package com.andi.carikopi.service;

import com.andi.carikopi.dto.CoffeeShopRequest;
import com.andi.carikopi.dto.CoffeeShopResponse;
import com.andi.carikopi.dto.StorageFileResponse;
import com.andi.carikopi.dto.UserResponse;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.entity.CoffeeShop;
import com.andi.carikopi.entity.StorageFile;
import com.andi.carikopi.entity.User;
import com.andi.carikopi.repository.CoffeeShopRepository;
import com.andi.carikopi.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CoffeeShopService {
    @Autowired
    private CoffeeShopRepository coffeeShopRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StorageFileService storageFileService;

    @Value("${app.url}")
    private String appUrl;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public WebResponse<CoffeeShopResponse> createProfile(CoffeeShopRequest request, String username, UUID shopId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CoffeeShop coffeeShop;
        if (shopId != null) {
            coffeeShop = coffeeShopRepository.findById(shopId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee shop not found"));
        } else {
            coffeeShop = new CoffeeShop();
        }

        coffeeShop.setNamaToko(request.getNamaToko());
        coffeeShop.setAlamat(request.getAlamat());
        coffeeShop.setDeskripsi(request.getDeskripsi());
        coffeeShop.setUser(user);
        coffeeShop.setTags(request.getTags());

        if (request.getFotoProfil() != null) {
            UUID fileId = storageFileService.uploadFile(request.getFotoProfil(), "foto_profil");
            coffeeShop.setFotoProfil(fileId);
        }

        if (request.getLatitude() != null && request.getLongitude() != null) {
            Point point = geometryFactory.createPoint(
                    new Coordinate(request.getLongitude(), request.getLatitude()));
            coffeeShop.setLocation(point);
        }

        coffeeShopRepository.save(coffeeShop);

        return getCoffeeShopResponseWebResponse(user, coffeeShop, null);
    }

    public WebResponse<CoffeeShopResponse> getMyCoffeeShop(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CoffeeShop coffeeShop = coffeeShopRepository.findCoffeeShopByUser(user);

        return getCoffeeShopResponseWebResponse(user, coffeeShop, null);
    }

    public WebResponse<List<CoffeeShopResponse>> findNearby(double lat, double lng, double radiusKm) {
        double radiusMeters = radiusKm * 1000;

        List<Object[]> results = coffeeShopRepository.findNearby(lat, lng, radiusMeters);

        List<CoffeeShopResponse> responses = new ArrayList<>();
        for (Object[] row : results) {
            CoffeeShopResponse response = CoffeeShopResponse.builder()
                    .id((UUID) row[0])
                    .namaToko((String) row[2])
                    .alamat((String) row[3])
                    .deskripsi((String) row[4])
                    .tags((String) row[5])
                    .build();

            // Extract lat/lng from the Point geometry (native queries return Geolatte types)
            if (row[7] != null) {
                org.geolatte.geom.Point<?> point = (org.geolatte.geom.Point<?>) row[7];
                response.setLatitude(point.getPosition().getCoordinate(1));
                response.setLongitude(point.getPosition().getCoordinate(0));
            }

            // Distance in meters (last column from query)
            if (row[row.length - 1] != null) {
                response.setDistance(((Number) row[row.length - 1]).doubleValue() / 1000);
            }

            responses.add(response);
        }

        return WebResponse.<List<CoffeeShopResponse>>builder()
                .code(200)
                .status("OK")
                .data(responses)
                .build();
    }

    private WebResponse<CoffeeShopResponse> getCoffeeShopResponseWebResponse(User user, CoffeeShop coffeeShop,
            Double distance) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        StorageFile file = storageFileService.findById(coffeeShop.getFotoProfil());
        String fullUrl = appUrl + "api/files/" + file.getId();
        StorageFileResponse storageFileResponse = StorageFileResponse.builder()
                .id(file.getId())
                .url(fullUrl)
                .filename(file.getFilename())
                .build();
        CoffeeShopResponse.CoffeeShopResponseBuilder builder = CoffeeShopResponse.builder()
                .id(coffeeShop.getId())
                .namaToko(coffeeShop.getNamaToko())
                .alamat(coffeeShop.getAlamat())
                .deskripsi(coffeeShop.getDeskripsi())
                .user(userResponse)
                .tags(coffeeShop.getTags())
                .fotoProfil(storageFileResponse);

        if (coffeeShop.getLocation() != null) {
            builder.latitude(coffeeShop.getLocation().getY());
            builder.longitude(coffeeShop.getLocation().getX());
        }

        if (distance != null) {
            builder.distance(distance);
        }

        return WebResponse.<CoffeeShopResponse>builder()
                .code(200)
                .status("OK")
                .data(builder.build())
                .build();
    }

    public WebResponse<String> uploadFotoProfil(MultipartFile file, UUID shopId) {
        CoffeeShop coffeeShop = coffeeShopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee shop not found"));

        if (coffeeShop == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee shop not found");
        }

        UUID fileId = storageFileService.uploadFile(file, "foto_profil");
        coffeeShop.setFotoProfil(fileId);
        coffeeShopRepository.save(coffeeShop);

        return WebResponse.<String>builder()
                .code(200)
                .status("OK")
                .data("Foto profil berhasil diunggah")
                .build();
    }

    public WebResponse<String> deleteFotoProfil(UUID shopId) {
        CoffeeShop coffeeShop = coffeeShopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee shop not found"));

        storageFileService.deleteFile(coffeeShop.getFotoProfil());
        coffeeShop.setFotoProfil(null);
        coffeeShopRepository.save(coffeeShop);

        return WebResponse.<String>builder()
                .code(200)
                .status("OK")
                .data("Foto profil berhasil dihapus")
                .build();
    }
}
