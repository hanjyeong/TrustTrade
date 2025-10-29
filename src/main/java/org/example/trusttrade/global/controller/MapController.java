package org.example.trusttrade.global.controller;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.global.dto.GeoPoint;
import org.example.trusttrade.global.dto.ProductDetailDto;
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
    private final MapService mapService;

    // 일반 물품 전체 조회 (사용자 주소 기반 5km 이내의 모든 일반 물품 조회)
    @GetMapping("/list")
    public ResponseEntity<List<ProductResponseDto>> getProductsOnMapByAddress(@RequestParam("address") String address) {
        GeoPoint geocode = mapService.addressToGeocode(address);
        List<ProductResponseDto> products = productService.findProductsNearby(geocode.getLat(), geocode.getLng());
        return ResponseEntity.ok(products);
    }

   /* // 물품 상세 조회
    @GetMapping("/{itemId}")
    public ResponseEntity<ProductDetailDto> getProductDetail(@PathVariable Long itemId) {
        return ResponseEntity.ok(productService.getProductDetail(itemId));
    }*/



}
