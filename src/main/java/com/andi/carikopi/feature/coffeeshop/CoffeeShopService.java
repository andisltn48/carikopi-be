package com.andi.carikopi.feature.coffeeshop;
import com.andi.carikopi.feature.coffeeshop.dto.CoffeeShopRequest;
import com.andi.carikopi.feature.coffeeshop.dto.CoffeeShopResponse;
import com.andi.carikopi.feature.storage.dto.StorageFileResponse;
import com.andi.carikopi.feature.auth.dto.UserResponse;
import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.storage.StorageFile;
import com.andi.carikopi.feature.auth.User;
import com.andi.carikopi.feature.auth.UserRepository;
import com.andi.carikopi.feature.storage.StorageFileService;
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
        coffeeShop.setCity(request.getCity());
        coffeeShop.setInstagram(request.getInstagram());
        coffeeShop.setTiktok(request.getTiktok());
        coffeeShop.setWhatsapp(request.getWhatsapp());
        coffeeShop.setFacebook(request.getFacebook());
        coffeeShop.setTwitter(request.getTwitter());
        coffeeShop.setXenditApiKey(request.getXenditApiKey());
        coffeeShop.setXenditCallbackToken(request.getXenditCallbackToken());

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

    public WebResponse<List<CoffeeShopResponse>> find(double lat, double lng, double radiusKm, String query) {
        double radiusMeters = radiusKm * 1000;

        List<Object[]> results;
        if (query != null && !query.isBlank()) {
            results = coffeeShopRepository.findNearbyByName(lat, lng, radiusMeters, query.trim());
        } else {
            results = coffeeShopRepository.findNearby(lat, lng, radiusMeters);
        }

        List<CoffeeShopResponse> responses = new ArrayList<>();
        for (Object[] row : results) {
            // Mapping based on explicit SELECT in Native Query:
            // 0=id, 1=alamat, 2=deskripsi, 3=foto_profil, 4=location, 
            // 5=nama_toko, 6=tags, 7=city, 8=instagram, 9=tiktok, 
            // 10=whatsapp, 11=facebook, 12=twitter, 13=jarak
            CoffeeShopResponse response = CoffeeShopResponse.builder()
                    .id((UUID) row[0])
                    .alamat((String) row[1])
                    .deskripsi((String) row[2])
                    .namaToko((String) row[5])
                    .tags((String) row[6])
                    .city((String) row[7])
                    .instagram((String) row[8])
                    .tiktok((String) row[9])
                    .whatsapp((String) row[10])
                    .facebook((String) row[11])
                    .twitter((String) row[12])
                    .build();

            // foto_profil (UUID at index 3)
            if (row[3] != null) {
                UUID fotoProfilId = (UUID) row[3];
                StorageFile file = storageFileService.findById(fotoProfilId);
                String fullUrl = appUrl + "api/files/" + file.getId();
                StorageFileResponse storageFileResponse = StorageFileResponse.builder()
                        .id(file.getId())
                        .url(fullUrl)
                        .filename(file.getFilename())
                        .build();
                response.setFotoProfil(storageFileResponse);
            }

            // Extract lat/lng from the Point geometry (native queries return Geolatte types)
            if (row[4] != null) {
                org.geolatte.geom.Point<?> point = (org.geolatte.geom.Point<?>) row[4];
                response.setLatitude(point.getPosition().getCoordinate(1));
                response.setLongitude(point.getPosition().getCoordinate(0));
            }

            // Distance in meters (jarak at index 13)
            if (row[13] != null) {
                response.setDistance(((Number) row[13]).doubleValue() / 1000);
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

        StorageFileResponse storageFileResponse = null;
        if (coffeeShop.getFotoProfil() != null) {
            StorageFile file = storageFileService.findById(coffeeShop.getFotoProfil());
            String fullUrl = appUrl + "api/files/" + file.getId();
            storageFileResponse = StorageFileResponse.builder()
                    .id(file.getId())
                    .url(fullUrl)
                    .filename(file.getFilename())
                    .build();
        }
        CoffeeShopResponse.CoffeeShopResponseBuilder builder = CoffeeShopResponse.builder()
                .id(coffeeShop.getId())
                .namaToko(coffeeShop.getNamaToko())
                .alamat(coffeeShop.getAlamat())
                .deskripsi(coffeeShop.getDeskripsi())
                .user(userResponse)
                .tags(coffeeShop.getTags())
                .fotoProfil(storageFileResponse)
                .city(coffeeShop.getCity())
                .instagram(coffeeShop.getInstagram())
                .tiktok(coffeeShop.getTiktok())
                .whatsapp(coffeeShop.getWhatsapp())
                .facebook(coffeeShop.getFacebook())
                .twitter(coffeeShop.getTwitter())
                .xenditApiKey(coffeeShop.getXenditApiKey())
                .xenditCallbackToken(coffeeShop.getXenditCallbackToken());

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

    public WebResponse<CoffeeShopResponse> getDetailCoffeeShopPublic(UUID shopId) {
        CoffeeShop coffeeShop = coffeeShopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee shop not found"));

        CoffeeShopResponse response = CoffeeShopResponse.builder()
                .id(coffeeShop.getId())
                .namaToko(coffeeShop.getNamaToko())
                .alamat(coffeeShop.getAlamat())
                .deskripsi(coffeeShop.getDeskripsi())
                .tags(coffeeShop.getTags())
                .city(coffeeShop.getCity())
                .instagram(coffeeShop.getInstagram())
                .tiktok(coffeeShop.getTiktok())
                .whatsapp(coffeeShop.getWhatsapp())
                .facebook(coffeeShop.getFacebook())
                .twitter(coffeeShop.getTwitter())
                .build();

        if (coffeeShop.getLocation() != null) {
            response.setLatitude(coffeeShop.getLocation().getY());
            response.setLongitude(coffeeShop.getLocation().getX());
        }

        StorageFile storageFile = storageFileService.findById(coffeeShop.getFotoProfil());
        String fullUrl = appUrl + "api/files/" + storageFile.getId();
        StorageFileResponse storageFileResponse = StorageFileResponse.builder()
                .id(storageFile.getId())
                .url(fullUrl)
                .filename(storageFile.getFilename())
                .build();
        response.setFotoProfil(storageFileResponse);

        return WebResponse.<CoffeeShopResponse>builder()
                .code(200)
                .status("OK")
                .data(response)
                .build();
    }
}
