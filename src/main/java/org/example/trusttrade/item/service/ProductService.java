package org.example.trusttrade.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.dto.StoredImage;
import org.example.trusttrade.item.dto.request.BasicItemDto;
import org.example.trusttrade.item.dto.response.ItemResponseDto;
import org.example.trusttrade.item.repository.ProductRepository;
import org.example.trusttrade.login.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ImageService imageService;
    private final ProductRepository productRepository;


    // 일반 물품 등록
    @Transactional
    public void registerBasicItem(BasicItemDto basicItemDto, User seller, List<MultipartFile> images) {
        List<StoredImage> storedImages = Collections.emptyList();
        try {

            // 이미지 변환
            storedImages = imageService.processImagesAndSetDto(images, basicItemDto);

            // Product 생성
            Product product = Product.fromDto(basicItemDto, seller);
            productRepository.save(product);

        } catch (Exception e) {
            // 업로드된 사진 삭제
            imageService.deleteFiles(storedImages);
            throw new RuntimeException("일반 물품 등록 중 오류 발생", e);
        }
    }

    // 일반 물품 전체 조회
    public List<ItemResponseDto> getBasicItems() {
        List<Product> items = productRepository.findAll();
        return items.stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getItemType(),
                        item.getDescription()
                ))
                .collect(Collectors.toList());
    }



}
