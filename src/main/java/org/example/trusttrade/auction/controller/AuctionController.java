package org.example.trusttrade.auction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.auction.domain.Auction;
import org.example.trusttrade.auction.domain.AuctionStatus;
import org.example.trusttrade.auction.dto.AuctionItemDto;
import org.example.trusttrade.auction.dto.AuctionResDto;
import org.example.trusttrade.auction.service.AuctionService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    @Autowired
    private final AuctionService auctionService;
    @Autowired
    private final UserRepository userRepository;

    //경매 생성
    @PostMapping("/new")
    public ResponseEntity<?> createAuction(@RequestBody AuctionItemDto auctionItemDto) {
        try {
            User seller = userRepository.findById(auctionItemDto.getSellerId()).orElseThrow();
            Auction auction = auctionService.registerAuction(auctionItemDto, seller);

            AuctionResDto response = AuctionResDto.fromEntity(auction);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //경매 조회 by 판매자
    @GetMapping("/{sellerId}/list")
    public ResponseEntity<?> auctionsBySellerId(@PathVariable("sellerId") UUID sellerId) {
        List<Auction> auctions = auctionService.getAuctionsBySeller(sellerId);

        List<AuctionResDto> response = auctions.stream()
                .map(AuctionResDto::fromEntity)
                .toList();

        return ResponseEntity.ok(response);

    }

    //경매 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> auctions() {

        List<Auction> auctions = auctionService.getAuctions();
        List<AuctionResDto> response = auctions.stream()
                .map(AuctionResDto::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    //경매 페이지 + 낙찰 페이지
    @GetMapping("/{auctionId}/details")
    public ResponseEntity<?> auctionDetails(@PathVariable("auctionId") Long auctionId) {
        try {
            Auction auction = auctionService.getAuctionById(auctionId);

            // 경매 상태에 따른 분기 처리
            if (auction.getAuctionStatus() == AuctionStatus.CLOSED) {
                return ResponseEntity.ok(Map.of(
                        "message", "경매가 종료된 상태입니다.",
                        "auction", AuctionResDto.fromEntity(auction)));
            }
            return ResponseEntity.ok(Map.of(
                    "message", "경매가 진행 중입니다.",
                    "auction", AuctionResDto.fromEntity(auction)));

        } catch (IllegalArgumentException e) {
            // 경매가 존재하지 않는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


}
