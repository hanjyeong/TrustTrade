package org.example.trusttrade.item.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponseDto {
    private Long item_id;
    private String name;
    private String itemType;
    private String description;

    public ItemResponseDto(Long item_id, String name, String itemType, String description) {
        this.item_id = item_id;
        this.name = name;
        this.itemType = itemType;
        this.description = description;
    }

}
