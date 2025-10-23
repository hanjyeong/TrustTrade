package org.example.trusttrade.global.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentResDto {

    private String orderId;
    private int amount;
    private String productName;

}
