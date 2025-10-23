package org.example.trusttrade.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SellerAccountDto {

    private String seller_account; // 사용자 아이디
    private Long item_id;
    private String itemType;
}
