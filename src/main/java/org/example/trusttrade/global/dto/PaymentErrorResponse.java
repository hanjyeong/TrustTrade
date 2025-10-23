package org.example.trusttrade.global.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentErrorResponse {
    private int code;
    private String message;
}
