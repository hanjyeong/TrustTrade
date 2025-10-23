package org.example.trusttrade.global.dto;

import lombok.Getter;
import org.example.trusttrade.item.domain.products.ProductLocation;

import java.time.LocalDateTime;

@Getter
public class AuctionUpdateDto {

    //Item 필드
    String name;
    String description;
    ProductLocation location;

    //Auction 필드
    int startPrice;
    int bidUnit;
    LocalDateTime endTime;


}
