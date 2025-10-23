package org.example.trusttrade.global.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

//결제 정보 임시 저장을 위한 엔티티
@Getter @Setter
public class SaveAmountRequest {
    private Long orderId;
    private int amount;
}
