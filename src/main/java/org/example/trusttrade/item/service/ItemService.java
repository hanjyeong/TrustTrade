package org.example.trusttrade.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.item.domain.ItemCategory;
import org.example.trusttrade.item.dto.request.CategoryDto;
import org.example.trusttrade.item.repository.*;
import org.example.trusttrade.item.dto.response.ItemResponseDto;
import org.example.trusttrade.global.repository.AuctionRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemImageRepository itemImageRepository;
    private final CategoryRepository categoryRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;

    // 일반 물품, 경매 물품 공통 기능 담당

    // 카테고리별 상품 조회
    public List<ItemResponseDto> findByCategoryAndType(CategoryDto categoryDto) {

        List<ItemCategory> itemCategories = itemCategoryRepository.findByCategory_Id(categoryDto.getCategoryId());

        return itemCategories.stream()
                .map(ItemCategory::getItem)
                .filter(item -> item.getItemType().equals(categoryDto.getItemType())) // "PRODUCT" or "AUCTION"
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getItemType(),
                        item.getDescription()
                ))
                .collect(Collectors.toList());
    }


}




