package org.example.trusttrade.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.item.domain.Category;
import org.example.trusttrade.item.domain.Item;
import org.example.trusttrade.item.domain.ItemCategory;
import org.example.trusttrade.item.domain.ItemImage;
import org.example.trusttrade.item.dto.request.CategoryDto;
import org.example.trusttrade.item.repository.*;
import org.example.trusttrade.item.dto.response.ItemResponseDto;
import org.example.trusttrade.repository.AuctionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void saveItemDetails(Item item,List<Integer> categoryIds) {

        // 카테고리 매핑
        if (categoryIds != null && !categoryIds.isEmpty()) {

            // 유효한 카테고리 ID 인지 조회
            List<Category> categories = categoryService.getValidCategories(categoryIds);
            List<Long> categoryIdList = categories.stream()
                    .map(Category::getId)
                    .toList();
            log.debug("유효 카테고리 조회 완료: itemId={}, categories={}", item.getId(), categoryIdList);

            List<ItemCategory> mappings = ItemCategory.createMappings(item, categories);
            log.debug("ItemCategory 매핑 생성 완료: itemId={}, mappingsCount={}", item.getId(), mappings.size());

            itemCategoryRepository.saveAll(mappings);
            log.debug("카테고리 매핑 저장 완료: itemId={}, savedMappings={}", item.getId(), mappings.size());
        } else {
            log.debug("매핑할 카테고리 없음: itemId={}", item.getId());
        }
    }


    // 물품 조회
    public List<ItemResponseDto> getItemByItemType(String itemType) {
        List<? extends Item> items;

        if ("PRODUCT".equalsIgnoreCase(itemType)) {
            items = productRepository.findAll();
        } else if ("AUCTION".equalsIgnoreCase(itemType)) {
            items = auctionRepository.findAll();
        } else {
            throw new IllegalArgumentException("존재하지 않는 아이템 타입 입니다");
        }

        return items.stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getItemType(),
                        item.getDescription()
                ))
                .collect(Collectors.toList());
    }

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




