package org.example.trusttrade.global.service;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.auction.Auction;
import org.example.trusttrade.auction.Bids;

import org.example.trusttrade.login.domain.User;

import org.example.trusttrade.global.dto.BidsResponseDto;
import org.example.trusttrade.global.repository.AuctionRepository;
import org.example.trusttrade.global.repository.BidRepository;
import org.example.trusttrade.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidService {

    @Autowired private final BidRepository bidRepository;
    @Autowired private final AuctionRepository auctionRepository;
    @Autowired private final UserRepository userRepository;

    //bid 생성
    public Bids creatBid(UUID bidderId, Long auctionId, int bidPrice){
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("경매를 찾을 수 없습니다."));

        User bidder = userRepository.findById(bidderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Id의 사용자를 찾을 수 없습니다."));

        Bids bid  = Bids.create(auction, bidder, bidPrice);
        bidRepository.save(bid);

        return bid;
    }

    //top5 입찰 조회
    public List<BidsResponseDto> getTop5BidsByAuction(Long auctionId) {
        List<Bids> top5Bids = bidRepository.findTop5ByAuctionIdOrderByBidPriceDesc(auctionId);
        return top5Bids.stream()
                .map(BidsResponseDto::from)
                .collect(Collectors.toList());
    }

}
