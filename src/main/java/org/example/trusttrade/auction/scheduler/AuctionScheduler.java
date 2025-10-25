package org.example.trusttrade.auction.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.auction.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {
    private final AuctionService auctionService;

    // 매 1분마다 실행
    @Scheduled(fixedRate = 60000)
    public void checkAndCloseAuctions() {
        auctionService.closeExpiredAuctions();
        System.out.println("==Auctions closed==");
    }
}
