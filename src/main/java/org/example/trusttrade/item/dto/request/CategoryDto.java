package org.example.trusttrade.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    Long categoryId;
    String itemType; // 일반, 경매 물품 구분
}
