package org.example.trusttrade.global.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmPaymentRequest {
    private int amount;
    private String orderId;
    private String paymentKey;
}
