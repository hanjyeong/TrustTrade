package org.example.trusttrade.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductTitleDto {

    private Long item_id;
    private String title; // 물품명
}
