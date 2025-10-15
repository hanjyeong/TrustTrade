package org.example.trusttrade.item.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "main_Image", nullable = false)
    private boolean main_Image; // 해당 사진이 대표 이미지인지를 구분하기 위함

    @Column(name = "saved_time", nullable = false)
    private LocalDateTime savedTime;

    // itemImage 객체 생성(대표이미지,서브 이미지 분리)
    public static List<ItemImage> fromMainAndSub(Item item, String mainImage, List<String> subImages) {

        List<ItemImage> result = new ArrayList<>();

        // 대표 이미지
        if (mainImage != null) {
            result.add(ItemImage.builder()
                    .imageUrl(mainImage)
                    .main_Image(true)
                    .item(item)
                    .savedTime(LocalDateTime.now())
                    .build());
        }

        // 서브 이미지
        if (subImages != null) {
            subImages.forEach(url -> result.add(
                    ItemImage.builder()
                            .imageUrl(url)
                            .main_Image(false)
                            .item(item)
                            .savedTime(LocalDateTime.now())
                            .build()
            ));
        }

        return result;
    }
}

