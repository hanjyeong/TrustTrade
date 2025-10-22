package org.example.trusttrade.item.repository;

import org.example.trusttrade.item.domain.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(p.productLocation.latitude)) " +
            "* cos(radians(p.productLocation.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(p.productLocation.latitude)))) <= 5")
    List<Product> findNearby(@Param("lat") double lat,
                             @Param("lng") double lng);

}


