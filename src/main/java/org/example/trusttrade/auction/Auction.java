package org.example.trusttrade.auction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.example.trusttrade.item.domain.Item;
import org.example.trusttrade.item.domain.products.ProductLocation;

import org.example.trusttrade.login.domain.User;

import org.example.trusttrade.item.dto.request.AuctionItemDto;
import org.example.trusttrade.global.dto.AuctionUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
@DiscriminatorValue("AUCTION")
@Table(name = "auction")
public class Auction extends Item {

    @Column(name = "start_price",nullable = false)
    private Integer startPrice;

    @Column(name = "bid_unit",nullable = false)
    private Integer bidUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status",nullable = false)
    private AuctionStatus auctionStatus;

    @Column(name = "end_time",nullable = false)
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "auction")
    private List<Bids> bids;


    public static Auction fromDto(AuctionItemDto dto, User seller){

        // 위치 정보 생성/조회
        ProductLocation loc = ProductLocation.fromDto(dto);

        // Auction 엔티티
        return Auction.builder()
                // Item
                .user(seller)
                .name(dto.getName())
                .description(dto.getDescription())
                .productLocation(loc)
                .createdTime(LocalDateTime.now())

                // Auction
                .startPrice(dto.getStartPrice())
                .bidUnit(dto.getBidUnit())
                .auctionStatus(AuctionStatus.OPEN)
                .endTime(dto.getEndTime())
                .build();
    }

    //경매 정보 업데이트
    public void updateAuction(AuctionUpdateDto auctionUpdateDto){
        if(this.hasBidder()){
            throw new IllegalStateException("입찰자가 존재하여 수정이 불가합니다.");
        }
        //location 객체 수정 추가 필요
        updateItem(auctionUpdateDto.getName(), auctionUpdateDto.getDescription());

        this.startPrice = auctionUpdateDto.getStartPrice();
        this.bidUnit = auctionUpdateDto.getBidUnit();
        this.endTime = auctionUpdateDto.getEndTime();

    }

    //경매 삭제시, bids 존재 여부 리턴
    public boolean hasBidder(){
        return this.bids != null && !this.bids.isEmpty();
    }

}
