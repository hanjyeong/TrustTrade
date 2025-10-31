package org.example.trusttrade.item.domain.products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.trusttrade.item.domain.Item;
import org.example.trusttrade.item.dto.request.BasicItemDto;

import org.example.trusttrade.login.domain.User;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
@DiscriminatorValue("PRODUCT")
@Table(name = "product")
public class Product extends Item {

    @Column(name="product_price")
    private Integer productPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status",nullable = false)
    private ProductStatus status;

    public static Product fromDto(BasicItemDto dto, User seller) {
        // 1) ProductLocation  생성
        ProductLocation loc = ProductLocation.fromDto(dto);

        // 2) Product 객체 생성
        return Product.builder()
                // Item 필드
                .user(seller)
                .name(dto.getTitle())
                .description(dto.getDescription())
                .productLocation(loc)
                .createdTime(LocalDateTime.now())

                // Product 필드
                .productPrice(dto.getPrice())
                .status(ProductStatus.SALE)
                .build();


    }

}