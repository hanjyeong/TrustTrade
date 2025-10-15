package org.example.trusttrade.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.auction.Auction;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.dto.AuctionItemDto;
import org.example.trusttrade.repository.AuctionRepository;
import org.example.trusttrade.item.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemService itemService;

   /* @Transactional
    public void registerAuction(AuctionItemDto dto, User seller) {
        // 1) Auction 객체 생성
        Auction auction = Auction.fromDto(dto, seller);
        log.debug("Auction 객체 생성 완료: {}", auction);
        auctionRepository.save(auction);
        log.debug("Auction 저장 완료: auctionId={}", auction.getId());

        // 2) 이미지·카테고리 저장
        itemService.saveItemDetails(auction, dto.getImages(), dto.getCategoryIds());
        log.debug("이미지·카테고리 저장 완료: auctionId={}", auction.getId());
    }
*/
    //경매 조죄 by sellerId
    @Transactional
    public List<Auction> getAuctionsBySeller(UUID sellerId) {
        return auctionRepository.getAuctionsBySellerId(sellerId);
    }




}
