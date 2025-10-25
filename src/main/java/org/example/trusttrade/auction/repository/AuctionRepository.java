package org.example.trusttrade.auction.repository;

import org.example.trusttrade.auction.domain.Auction;
import org.example.trusttrade.auction.domain.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<Auction, Long> {


    @Query("SELECT a FROM Auction a WHERE a.user.id = :sellerId")
    List<Auction> getAuctionsBySellerId(@Param("sellerId") UUID sellerId);

    //마감되지 않은 경매 중 현재시간이 end 시간을 지난 경매 조회
    List<Auction> findByEndTimeBeforeAndAuctionStatus(LocalDateTime now, AuctionStatus status);


}
