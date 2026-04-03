package com.andi.carikopi.repository;

import com.andi.carikopi.entity.CoffeeShop;
import com.andi.carikopi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CoffeeShopRepository extends JpaRepository<CoffeeShop, UUID> {
    CoffeeShop findCoffeeShopByUser(User user);

    @Query(value = """
            SELECT cs.*, ST_Distance(
                cs.location::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
            ) AS jarak
            FROM coffeeshops cs
            WHERE cs.location IS NOT NULL
            AND ST_DWithin(
                cs.location::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                :radiusMeters
            )
            ORDER BY jarak ASC
            """, nativeQuery = true)
    List<Object[]> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") double radiusMeters
    );

    @Query(value = """
            SELECT cs.*, ST_Distance(
                cs.location::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
            ) AS jarak
            FROM coffeeshops cs
            WHERE cs.location IS NOT NULL
            AND cs.nama_toko ILIKE CONCAT('%', :query, '%')
            AND ST_DWithin(
                cs.location::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                :radiusMeters
            )
            ORDER BY jarak ASC
            """, nativeQuery = true)
    List<Object[]> findNearbyByName(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") double radiusMeters,
            @Param("query") String query
    );
}
