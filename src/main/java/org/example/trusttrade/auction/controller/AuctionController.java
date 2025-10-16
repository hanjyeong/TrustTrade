package org.example.trusttrade.auction.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.auction.domain.Auction;
import org.example.trusttrade.auction.dto.AuctionItemDto;
import org.example.trusttrade.auction.dto.AuctionUpdateDto;
import org.example.trusttrade.auction.repository.AuctionRepository;
import org.example.trusttrade.auction.service.AuctionService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.repository.UserRepository;
import org.example.trusttrade.login.service.UserService;
import org.example.trusttrade.order.dto.OrderReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    @Autowired private final AuctionService auctionService;
    @Autowired private final AuctionRepository auctionRepository;
    @Autowired private final UserRepository userRepository;

    //경매 생성
    @PostMapping("/{sellerId}/new")
    public ResponseEntity<?> createAuction(AuctionItemDto auctionItemDto,
                                           @PathVariable("sellerId")UUID sellerId) {
        try{
            User seller = userRepository.findById(sellerId).orElseThrow();
            auctionService.registerAuction(auctionItemDto, seller);

            return ResponseEntity.ok("경매 생성 완료");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


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
}
