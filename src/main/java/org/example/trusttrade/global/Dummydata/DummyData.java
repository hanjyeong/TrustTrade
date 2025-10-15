package org.example.trusttrade.global.Dummydata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.item.domain.Category;
import org.example.trusttrade.item.domain.ItemCategory;
import org.example.trusttrade.item.domain.ItemImage;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.domain.products.ProductLocation;
import org.example.trusttrade.item.domain.products.ProductStatus;
import org.example.trusttrade.auction.Auction;
import org.example.trusttrade.auction.AuctionStatus;
import org.example.trusttrade.repository.AuctionRepository;
import org.example.trusttrade.item.repository.CategoryRepository;
import org.example.trusttrade.item.repository.ItemCategoryRepository;
import org.example.trusttrade.item.repository.ItemImageRepository;
import org.example.trusttrade.item.repository.ProductRepository;

import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.domain.User.MemberType;
import org.example.trusttrade.login.domain.User.Role;

import org.example.trusttrade.login.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DummyData {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    @PostConstruct
    public void init() {
        createDummyUser();
        createDummyBusinessUser();
        createDummyCategories();
        createDummyProducts();
        createDummyAuctions();
    }

    private void createDummyUser() {
        User dummyUser = User.builder()
                .id(UUID.fromString("ffd9c396-b70e-4d59-8d04-fad7b1fa1df2"))
                .email("dummyuser@example.com")
                .profileImage("https://example.com/image.png")
                .role(Role.USER)
                .memberType(MemberType.GENERAL)
                .roughAddress("서울특별시 강남구")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        userRepository.save(dummyUser);
    }

    private void createDummyBusinessUser() {
        User dummyBusinessUser = User.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .email("businessuser@example.com")
                .profileImage("https://example.com/business.png")
                .role(Role.USER)
                .memberType(MemberType.BUSINESS)
                .roughAddress("서울특별시 서초구")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        userRepository.save(dummyBusinessUser);
    }

    private void createDummyCategories() {
        Category category1 = Category.builder().categoryName("전자제품").build();
        Category category2 = Category.builder().categoryName("주방용품").build();
        Category category3 = Category.builder().categoryName("가구").build();
        Category category4 = Category.builder().categoryName("의류").build();
        Category category5 = Category.builder().categoryName("기타").build();
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
        categoryRepository.save(category5);

    }

    private void createDummyProducts() {
        User user = userRepository.findById(
                UUID.fromString("ffd9c396-b70e-4d59-8d04-fad7b1fa1df2")
        ).orElseThrow();
        Category cat1 = categoryRepository.findById(1).orElseThrow();
        Category cat2 = categoryRepository.findById(2).orElseThrow();

        // 3개의 서로 다른 ProductLocation 생성
        List<ProductLocation> locations = new ArrayList<>();
        locations.add(ProductLocation.builder()
                .address("서울특별시 강남구 예시동 1")
                .latitude(37.4970)
                .longitude(127.0270)
                .build());
        locations.add(ProductLocation.builder()
                .address("서울특별시 강남구 예시동 2")
                .latitude(37.4980)
                .longitude(127.0280)
                .build());
        locations.add(ProductLocation.builder()
                .address("서울특별시 강남구 예시동 3")
                .latitude(37.4990)
                .longitude(127.0290)
                .build());

        // 각 Product 생성 시 순차적으로 다른 location 적용
        for (int i = 1; i <= 3; i++) {
            ProductLocation loc = locations.get(i - 1);
            Product product = Product.builder()
                    .user(user)
                    .name("일반 상품 " + i)
                    .description("일반 사용자가 등록한 상품 " + i)
                    .productLocation(loc)
                    .createdTime(LocalDateTime.now())
                    .productPrice(10000 * i)
                    .status(ProductStatus.SALE)
                    .build();
            productRepository.save(product);

            List<ItemImage> images = List.of(
                    ItemImage.builder()
                            .item(product)
                            .imageUrl("https://example.com/img" + (2 * i - 1) + ".jpg")
                            .main_Image(true)
                            .savedTime(LocalDateTime.now())
                            .build(),

                    ItemImage.builder()
                            .item(product)
                            .imageUrl("https://example.com/img" + (2 * i) + ".jpg")
                            .main_Image(false)
                            .savedTime(LocalDateTime.now())
                            .build()
            );

            itemImageRepository.saveAll(images);

            List<ItemCategory> mappings = List.of(
                    ItemCategory.builder().item(product).category(cat1).build(),
                    ItemCategory.builder().item(product).category(cat2).build()
            );
            itemCategoryRepository.saveAll(mappings);
        }
    }


    private void createDummyAuctions() {
        User business = userRepository.findById(
                UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        ).orElseThrow();
        Category cat1 = categoryRepository.findById(1).orElseThrow();
        Category cat2 = categoryRepository.findById(3).orElseThrow();

        // 3개의 서로 다른 ProductLocation 생성
        List<ProductLocation> auctionLocations = new ArrayList<>();
        auctionLocations.add(ProductLocation.builder()
                .address("서울특별시 서초구 예시동 A")
                .latitude(37.4830)
                .longitude(127.0320)
                .build());
        auctionLocations.add(ProductLocation.builder()
                .address("서울특별시 서초구 예시동 B")
                .latitude(37.4840)
                .longitude(127.0330)
                .build());
        auctionLocations.add(ProductLocation.builder()
                .address("서울특별시 서초구 예시동 C")
                .latitude(37.4850)
                .longitude(127.0340)
                .build());

        // 각 Auction 생성 시 순차적으로 다른 location 적용
        for (int i = 1; i <= 3; i++) {
            ProductLocation loc = auctionLocations.get(i - 1);
            Auction auction = Auction.builder()
                    .user(business)
                    .name("경매 상품 " + i)
                    .description("비즈니스 사용자가 등록한 경매 상품 " + i)
                    .productLocation(loc)
                    .createdTime(LocalDateTime.now())
                    .startPrice(50000 * i)
                    .bidUnit(5000)
                    .auctionStatus(AuctionStatus.OPEN)
                    .endTime(LocalDateTime.now().plusDays(7))
                    .build();
            auctionRepository.save(auction);

            List<ItemImage> images = List.of(
                    ItemImage.builder()
                            .item(auction)
                            .imageUrl("https://example.com/auction" + (2 * i - 1) + ".jpg")
                            .main_Image(true)   // 대표 이미지
                            .savedTime(LocalDateTime.now())
                            .build(),

                    ItemImage.builder()
                            .item(auction)
                            .imageUrl("https://example.com/auction" + (2 * i) + ".jpg")
                            .main_Image(false)  // 서브 이미지
                            .savedTime(LocalDateTime.now())
                            .build()
            );

            itemImageRepository.saveAll(images);

            List<ItemCategory> mappings = List.of(
                    ItemCategory.builder().item(auction).category(cat1).build(),
                    ItemCategory.builder().item(auction).category(cat2).build()
            );
            itemCategoryRepository.saveAll(mappings);
        }
    }
}
