package org.example.trusttrade.global.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.auction.Auction;
import org.example.trusttrade.global.dto.AuctionUpdateDto;
import org.example.trusttrade.item.dto.request.AuctionItemDto;
import org.example.trusttrade.item.dto.request.CategoryDto;
import org.example.trusttrade.item.dto.request.SellerAccountDto;
import org.example.trusttrade.item.dto.response.ItemResponseDto;
import org.example.trusttrade.item.service.ItemService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.service.UserService;
import org.example.trusttrade.global.repository.AuctionRepository;
import org.example.trusttrade.global.service.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionRepository auctionRepository;
    private final ItemService itemService;
    private final UserService userService;


    //경매 조회 by 판매자
    @GetMapping("/{sellerId}/list")
    public ResponseEntity<?> auctionsBySellerId(@PathVariable("sellerId") UUID sellerId) {
        List<Auction> auctions = auctionService.getAuctionsBySeller(sellerId);
        return ResponseEntity.ok(auctions);

    }

    //경매 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> auctions() {
        return ResponseEntity.ok(auctionRepository.findAll());
    }

    //경매 삭제
    @PostMapping("/{auctionId}/delete")
    public ResponseEntity<?> deleteAuction(@PathVariable("auctionId") Long auctionId) {
        Optional<Auction> findAuction = auctionRepository.findById(auctionId);

        if (findAuction.isPresent()) {
            if(findAuction.get().hasBidder()){

                auctionRepository.delete(findAuction.get());
                return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
            }else{
                return ResponseEntity.ok("입찰자가 존재하여 삭제가 불가능합니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 상품을 찾을 수 없습니다.");
        }
    }

    //경매 수정
    @PostMapping("{auctionId}/update")
    public ResponseEntity<?> updateAuction(@PathVariable("auctionId") Long auctionId,
                                           @RequestBody AuctionUpdateDto auctionUpdateDto) {
        Optional<Auction> findAuction = auctionRepository.findById(auctionId);

        if(findAuction.isPresent()) {
            findAuction.get().updateAuction(auctionUpdateDto);
            return ResponseEntity.ok("경매 정보가 성공적으로 수정되었습니다.");
        }else{
            return ResponseEntity.ok("해당 ID의 상품을 찾을 수 없습니다.");
        }
    }

    // 경매 물품 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerAuction(
            @RequestPart("auction_item") @Valid AuctionItemDto auctionItemDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        try {

            // 판매자 권한 검증
            User seller = userService.validateBusinessUser(auctionItemDto.getSellerId());

            // 경매 물품 등록
            auctionService.registerAuctionItem(auctionItemDto, seller, images);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("경매 물품이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            log.error("경매 물품 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("경매 물품 등록 실패: " + e.getMessage());
        }
    }

    /*// 경매 물품 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<ItemResponseDto>> getAuctionItems() {
        List<ItemResponseDto> items = auctionService.getAuctionItems();
        return ResponseEntity.ok(items);
    }*/

    // 카테고리별 조회
    @GetMapping("/category/list")
    public ResponseEntity<List<ItemResponseDto>> getItemsCategory(@RequestBody CategoryDto categoryDto) {
        List<ItemResponseDto> items = itemService.findByCategoryAndType(categoryDto);
        return ResponseEntity.ok(items);
    }

    // 판매자 이름별 조회
    @GetMapping("/seller/list")
    public ResponseEntity<List<ItemResponseDto>> getItemsSellerAccount(
            @RequestParam String sellerAccount,
            @RequestParam String itemType) {

        List<ItemResponseDto> items = itemService.findBySellerAccountAndType(sellerAccount, itemType);
        return ResponseEntity.ok(items);
    }



    // 물품명 조회

}
