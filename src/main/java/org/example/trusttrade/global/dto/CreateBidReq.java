package org.example.trusttrade.global.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBidReq {

    UUID user;
    Long auctionId;
    int bidPrice;
}
