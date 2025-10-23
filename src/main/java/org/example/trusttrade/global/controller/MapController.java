package org.example.trusttrade.global.controller;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.global.dto.GeoPoint;
import org.example.trusttrade.global.dto.ProductResponseDto;
import org.example.trusttrade.global.service.KakaoAddressSearchService;
import org.example.trusttrade.global.service.MapService;
import org.example.trusttrade.item.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class MapController {

    private final ProductService productService;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final MapService mapService;

    // 일반 물품 전체 조회 (사용자 위치 기반 5km 이내의 모든 일반 물품 조회)
    @GetMapping("/list")
    public ResponseEntity<List<ProductResponseDto>> getProductsOnMapByAddress(
            @RequestParam("address") String address
    ) {
        GeoPoint geocode = mapService.geocode(address);
        List<ProductResponseDto> products = productService.findProductsNearby(geocode.getLat(), geocode.getLng());
        return ResponseEntity.ok(products);
    }

    private double toDouble(Object v) {
        if (v instanceof Number n) return n.doubleValue();
        return Double.parseDouble(String.valueOf(v));
    }

}
