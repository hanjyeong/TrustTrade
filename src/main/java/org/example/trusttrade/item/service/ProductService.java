package org.example.trusttrade.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.item.domain.ItemImage;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.dto.request.BasicItemDto;
import org.example.trusttrade.item.repository.ItemImageRepository;
import org.example.trusttrade.item.repository.ItemRepository;
import org.example.trusttrade.item.repository.ProductRepository;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.service.UserService;

import org.example.trusttrade.login.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ItemService itemService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemImageRepository itemImageRepository;

    @Transactional
    public void registerProduct(BasicItemDto dto, User seller) {

        Product product = Product.fromDto(dto, seller);
        productRepository.save(product);
        log.debug("Product 저장 완료: itemId={}", product.getId());

        // 이미지 url 저장
        List<ItemImage> itemImages = ItemImage.fromMainAndSub(product, dto.getMainImage(), dto.getSubImages());
        itemImageRepository.saveAll(itemImages);

        // 카테고리 url 저장
        itemService.saveItemDetails(product,dto.getCategoryIds());
    }

}
