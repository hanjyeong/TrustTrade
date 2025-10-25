package org.example.trusttrade.auction.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.auction.domain.Auction;
import org.example.trusttrade.auction.domain.AuctionStatus;
import org.example.trusttrade.auction.dto.AuctionItemDto;
import org.example.trusttrade.auction.repository.AuctionRepository;
import org.example.trusttrade.auction.repository.BidRepository;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.item.service.ItemService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemService itemService;
    private final BidRepository bidRepository;

    @Transactional
    public Auction registerAuction(AuctionItemDto dto, User seller) {
        // 1) Auction 객체 생성
        Auction auction = Auction.fromDto(dto, seller);
        log.debug("Auction 객체 생성 완료: {}", auction);
        auctionRepository.save(auction);
        log.debug("Auction 저장 완료: auctionId={}", auction.getId());

        // 2) 이미지·카테고리 저장
        itemService.saveItemDetails(auction, dto.getImages(), dto.getCategoryIds());
        log.debug("이미지·카테고리 저장 완료: auctionId={}", auction.getId());

        return auction;
    }

    //경매 조죄 by sellerId
    @Transactional
    public List<Auction> getAuctionsBySeller(UUID sellerId) {
        return auctionRepository.getAuctionsBySellerId(sellerId);
    }

    //경매 목록 조회
    public List<Auction> getAuctions() {
        return auctionRepository.findAll();
    }

    //단일 경매 상세 정보 조회
    public Auction getAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("경매가 존재하지 않습니다.  " + auctionId));
    }

    // 마감된 경매 처리
    @Transactional
    public void closeExpiredAuctions() {
        List<Auction> expiredAuctions = auctionRepository.findByEndTimeBeforeAndAuctionStatus(LocalDateTime.now(), AuctionStatus.OPEN);

        for (Auction auction : expiredAuctions) {
            bidRepository.findTopByAuctionOrderByBidPriceDesc(auction)
                    .ifPresent(bid -> auction.setWinner(bid.getUser()));

            auction.setAuctionStatus(AuctionStatus.CLOSED);

            System.out.println("Auction " + auction.getId() + " closed. Winner: " +
                    (auction.getWinner() != null ? auction.getWinner().getId() : "None"));

        }
    }

}
