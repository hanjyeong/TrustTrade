package org.example.trusttrade.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderReqDto {

    private Long productId;
    private UUID buyerId;
    private UUID sellerId;


}
