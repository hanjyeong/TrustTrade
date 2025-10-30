package org.example.trusttrade.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.item.dto.StoredImage;
import org.example.trusttrade.item.service.ImageService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.auction.dto.AuctionItemDto;
import org.example.trusttrade.item.dto.request.BasicItemDto;
import org.example.trusttrade.auction.service.AuctionService;
import org.example.trusttrade.item.service.ItemService;
import org.example.trusttrade.item.service.ProductService;

import org.example.trusttrade.login.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ItemService itemService;
    private final ProductService productService;
    private final UserService userService;
    private final AuctionService auctionService;
    private final ImageService imageService;
    // 일반 물품 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerBasicProduct(
            @RequestPart("item") @Valid BasicItemDto basicItemDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        List<StoredImage> storedImages = Collections.emptyList();

        try {

            // 판매자 권한 검증
            User seller = userService.validateBusinessUser(basicItemDto.getSellerId());

            // 일반 물품 등록
            productService.registerBasicItem(basicItemDto,seller,images);

            return ResponseEntity.status(HttpStatus.CREATED).body("일반 물품이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            log.error("물품 등록 중 오류 발생", e);

            imageService.deleteFiles(storedImages);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("물품 등록 실패: " + e.getMessage());
        }
    }

    // 경매 물품 등록
    @PostMapping("/register/auctionItem")
    public ResponseEntity<String> registerAuction(@Valid @RequestBody AuctionItemDto dto) {
        try {
            // 판매자 BUSINESS 권한 검증
            User seller = userService.validateBusinessUser(dto.getSellerId());
            // 경매 물품 등록
            auctionService.registerAuction(dto, seller);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("경매 물품이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("경매 물품 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("경매 물품 등록 실패: " + e.getMessage());
        }
    }


}
