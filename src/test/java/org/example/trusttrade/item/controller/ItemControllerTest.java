/*
package org.example.trusttrade.item.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.trusttrade.item.domain.ItemImage;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.repository.ItemCategoryRepository;
import org.example.trusttrade.item.repository.ItemImageRepository;
import org.example.trusttrade.item.repository.ProductRepository;
import org.example.trusttrade.item.dto.request.BasicItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemImageRepository itemImageRepository;

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("일반 상품 등록 - 이미지 3장 + 카테고리 2개 등록 성공")
    void registerBasicProduct_success() throws Exception {
        // given (프론트에서 보내는 JSON)
        BasicItemDto dto = new BasicItemDto();
        dto.setTitle("테스트 상품");
        dto.setDescription("테스트 상품 설명입니다.");
        dto.setPrice(20000);
        dto.setSellerId(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")); // 더미 비즈니스 유저
        dto.setCategoryIds(List.of(1, 2));
        dto.setAddress("서울특별시 서초구 테스트로 123");
        dto.setLatitude(37.4845);
        dto.setLongitude(127.0335);

        String dtoJson = objectMapper.writeValueAsString(dto);

        MockMultipartFile itemPart = new MockMultipartFile(
                "item",
                "item",
                MediaType.APPLICATION_JSON_VALUE,
                dtoJson.getBytes(StandardCharsets.UTF_8)
        );

        // Mock 이미지 3장 업로드
        MockMultipartFile image1 = new MockMultipartFile(
                "images", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile(
                "images", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-2".getBytes());
        MockMultipartFile image3 = new MockMultipartFile(
                "images", "image3.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-3".getBytes());

        // when
        mockMvc.perform(
                        multipart("/products/register/basic")
                                .file(itemPart)
                                .file(image1)
                                .file(image2)
                                .file(image3)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());

        // then
        List<Product> products = productRepository.findAll();
        assertThat(products).isNotEmpty();

        Product savedProduct = products.get(products.size() - 1);
        assertThat(savedProduct.getName()).isEqualTo("테스트 상품");
        assertThat(savedProduct.getProductPrice()).isEqualTo(20000);

        // 이미지 3장 정상 저장 확인
        List<ItemImage> images = itemImageRepository.findAll();
        List<ItemImage> productImages = images.stream()
                .filter(img -> img.getItem().getId().equals(savedProduct.getId()))
                .toList();
        assertThat(productImages).hasSize(3);
        assertThat(productImages.stream().filter(ItemImage::isMain_Image).count()).isEqualTo(1);

        // 카테고리 매핑 정상 저장 확인
        long categoryMappingCount = itemCategoryRepository.count();
        assertThat(categoryMappingCount).isGreaterThan(0);
    }
}
*/
