package org.example.trusttrade.item.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String itemType;
    private String description;

    public CategoryResponseDto(Long id, String name, String itemType, String description) {
        this.id = id;
        this.name = name;
        this.itemType = itemType;
        this.description = description;
    }

}
