package org.example.trusttrade.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.auction.Auction;
import org.example.trusttrade.item.domain.ItemCategory;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.dto.request.CategoryDto;
import org.example.trusttrade.item.repository.*;
import org.example.trusttrade.item.dto.response.ItemResponseDto;
import org.example.trusttrade.global.repository.AuctionRepository;

import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final UserRepository userRepository;

    // 일반 물품, 경매 물품 공통 기능 담당 (카테고리, 판매자 아이디, 물품명 조회)

    // 카테고리별 상품 조회
    public List<ItemResponseDto> findByCategoryAndType(CategoryDto categoryDto) {

        List<ItemCategory> itemCategories = itemCategoryRepository.findByCategory_Id(categoryDto.getCategoryId());

        return itemCategories.stream()
                .map(ItemCategory::getItem) // 부모 Item
                .filter(item -> item.getItemType() != null  && item.getItemType().equalsIgnoreCase(categoryDto.getItemType()))
                .map(item -> {
                    if (item instanceof Product p) return ItemResponseDto.fromProduct(p);
                    if (item instanceof Auction a) return ItemResponseDto.fromAuction(a);
                    throw new IllegalStateException("지원하지 않는 아이템 타입: " + item.getClass().getSimpleName());
                })
                .toList();
    }


    // 판매자 아이디로 상품 조회
    @Transactional(readOnly = true)
    public List<ItemResponseDto> findBySellerAccountAndType(String sellerAccount, String itemType) {

        // 사용자 조회
        User seller = userRepository.findByUserAccount(sellerAccount)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 판매자 계정입니다."));

        // 일반 물품, 경매 물품 분리하여 상품 조회
        if ("PRODUCT".equalsIgnoreCase(itemType)) { // 일반 물품 조회
            return productRepository.getProductBySellerId(seller.getId())
                    .stream()
                    .map(ItemResponseDto::fromProduct)
                    .toList();
        } else if ("AUCTION".equalsIgnoreCase(itemType)) { // 경매 물품 조회
            return auctionRepository.getAuctionsBySellerId(seller.getId())
                    .stream()
                    .map(ItemResponseDto::fromAuction)
                    .toList();
        } else {
            throw new IllegalArgumentException("유효하지 않은 아이템 타입입니다: " + itemType);
        }
    }

    // 물품 이름으로 상품 조회
    @Transactional(readOnly = true)
    public List<ItemResponseDto> findByItemTitleAndType(String title, String itemType) {

        String type = itemType == null ? "" : itemType.trim().toUpperCase();

        // 일반 물품 조회
        if ("PRODUCT".equals(type)) {
            return productRepository.findByTitleContainingWithSeller(title).stream()
                    .map(ItemResponseDto::fromProduct)
                    .toList();
        }

        // 경매 물품 조회
        if ("AUCTION".equals(type)) {
            return auctionRepository.findByTitleContainingWithSeller(title).stream()
                    .map(ItemResponseDto::fromAuction)
                    .toList();
        }
        throw new IllegalArgumentException("허용되지 않는 itemType: " + itemType + " (PRODUCT|AUCTION 중 하나를 사용)");
    }


}




