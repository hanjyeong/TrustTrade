package org.example.trusttrade.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.item.dto.StoredImage;
import org.example.trusttrade.item.dto.request.CategoryDto;
import org.example.trusttrade.item.service.ImageService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.dto.AuctionItemDto;
import org.example.trusttrade.item.dto.request.BasicItemDto;
import org.example.trusttrade.item.dto.response.ItemResponseDto;
import org.example.trusttrade.item.dto.request.ItemTypeDto;
import org.example.trusttrade.service.AuctionService;
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
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ProductService productService;
    private final UserService userService;
    private final AuctionService auctionService;
    private final ImageService imageService;

    // 일반 물품 등록
    @PostMapping(value = "/register/basic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerBasicProduct(
            @RequestPart("item") @Valid BasicItemDto basicItemDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        List<StoredImage> storedImages = Collections.emptyList();

        try {
            // 1. 판매자 권한 검증
            User seller = userService.validateBusinessUser(basicItemDto.getSellerId());

            // 2. 이미지 업로드
            storedImages = imageService.uploadImages(images);

            // 3. URL만 DTO에 세팅 (대표/서브 분리)
            List<String> urls = storedImages.stream()
                    .map(StoredImage::getUrl)
                    .toList();

            if (!urls.isEmpty()) {
                basicItemDto.setMainImage(urls.get(0));
                if (urls.size() > 1) {
                    basicItemDto.setSubImages(urls.subList(1, urls.size()));
                }
            }

            // 4. 상품 등록
            productService.registerProduct(basicItemDto, seller);

            return ResponseEntity.status(HttpStatus.CREATED).body("일반 물품이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            log.error("물품 등록 중 오류 발생", e);

            // 5. 예외 발생 시 업로드한 이미지 삭제
            imageService.deleteFiles(storedImages);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("물품 등록 실패: " + e.getMessage());
        }
    }

   /* // 경매 물품 등록
    @PostMapping(value= "/register/auctionItem",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
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
    }*/

    // 일반 물품 or 경매 물품 조회
    @GetMapping("/list")
    public ResponseEntity<List<ItemResponseDto>> getItemsByType (@RequestBody ItemTypeDto dto){
        List<ItemResponseDto> items = itemService.getItemByItemType(dto.getItemType());
        return ResponseEntity.ok(items);
    }

    // 카테고리별 조회
    @GetMapping("/list/category")
    public ResponseEntity<List<ItemResponseDto>> getItemsCategory(@RequestBody CategoryDto categoryDto) {
        List<ItemResponseDto> items = itemService.findByCategoryAndType(categoryDto);
        return ResponseEntity.ok(items);
    }

    // 판매자 이름별 조회



    // 물품 이름 조회




}
