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
import org.example.trusttrade.global.repository.AuctionRepository;
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
        createMapTestProducts();
    }

    // ----------------------
    // 공통 유틸
    // ----------------------
    private ProductLocation buildLocation(String address, double lat, double lng) {
        return ProductLocation.builder()
                .address(address)
                .latitude(lat)
                .longitude(lng)
                .build();
    }

    // 지도 API 테스트용 오프셋 계산
    private double[] offsetLatLng(double lat, double lng, double metersNorth, double metersEast) {
        double metersPerDegLat = 111_320d;
        double metersPerDegLng = 111_320d * Math.cos(Math.toRadians(lat));
        double dLat = metersNorth / metersPerDegLat;
        double dLng = metersEast  / metersPerDegLng;
        return new double[]{ lat + dLat, lng + dLng };
    }

    /**
     * 지도 반경(예: 5km) 테스트용으로 중심점 근처에 상품 4개를 심는다.
     */
    private void createMapTestProducts() {
        User user = userRepository.findById(
                UUID.fromString("ffd9c396-b70e-4d59-8d04-fad7b1fa1df2")
        ).orElseThrow();

        Category cat1 = categoryRepository.findById(1).orElseThrow();
        Category cat2 = categoryRepository.findById(2).orElseThrow();

        final double centerLat = 37.4979;
        final double centerLng = 127.0276;

        double[] near300   = offsetLatLng(centerLat, centerLng, 0,  300);
        double[] near1500  = offsetLatLng(centerLat, centerLng, 0,  1500);
        double[] near4800  = offsetLatLng(centerLat, centerLng, 0,  4800);
        double[] out5200   = offsetLatLng(centerLat, centerLng, 0,  5200);

        ProductLocation locA = buildLocation("강남역 동쪽 300m 지점", near300[0], near300[1]);
        ProductLocation locB = buildLocation("강남역 동쪽 1.5km 지점", near1500[0], near1500[1]);
        ProductLocation locC = buildLocation("강남역 동쪽 4.8km 지점", near4800[0], near4800[1]);
        ProductLocation locD = buildLocation("강남역 동쪽 5.2km 지점 (반경 밖)", out5200[0], out5200[1]);

        saveProductWithImagesAndCategories(user, "지도테스트-A(300m)", "반경 5km 내 - 매우 가까움", 110000, locA, cat1, cat2);
        saveProductWithImagesAndCategories(user, "지도테스트-B(1.5km)", "반경 5km 내 - 가까움",   120000, locB, cat1, cat2);
        saveProductWithImagesAndCategories(user, "지도테스트-C(4.8km)", "반경 5km 내 - 경계부근", 130000, locC, cat1, cat2);
        saveProductWithImagesAndCategories(user, "지도테스트-D(5.2km)", "반경 5km 밖 - 제외대상", 140000, locD, cat1, cat2);
    }

    // 상품 + 이미지 2장 + 카테고리 2개 매핑 저장
    private void saveProductWithImagesAndCategories(
            User user,
            String name,
            String description,
            int price,
            ProductLocation location,
            Category cat1,
            Category cat2
    ) {
        Product product = Product.builder()
                .user(user)
                .name(name)
                .description(description)
                .productLocation(location)
                .createdTime(LocalDateTime.now())
                .productPrice(price)
                .status(ProductStatus.SALE)
                .build();
        productRepository.save(product);

        List<ItemImage> images = List.of(
                ItemImage.builder()
                        .item(product)
                        .imageUrl("https://example.com/" + name + "_1.jpg")
                        .main_Image(true)
                        .savedTime(LocalDateTime.now())
                        .build(),
                ItemImage.builder()
                        .item(product)
                        .imageUrl("https://example.com/" + name + "_2.jpg")
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

    // ----------------------
    // 더미 유저
    // ----------------------
    private void createDummyUser() {
        String encodedPw = "1234";

        // 변경 포인트: 문자열 주소가 아니라 ProductLocation 객체를 만들어 user_location에 세팅
        ProductLocation userLoc = buildLocation("서울특별시 강남구", 37.4979, 127.0276);

        User dummyUser = User.builder()
                .id(UUID.fromString("ffd9c396-b70e-4d59-8d04-fad7b1fa1df2"))
                .userAccount("dummyuser")
                .userPw(encodedPw)
                .email("dummyuser@example.com")
                .profileImage("https://example.com/image.png")
                .role(Role.USER)
                .memberType(MemberType.GENERAL)
                .user_location(userLoc)             // ★ 여기로 변경
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        userRepository.save(dummyUser); // Cascade.ALL로 userLoc도 함께 저장
    }

    private void createDummyBusinessUser() {
        String encodedPw = "1234";

        // 변경 포인트: roughAddress 제거, 동일하게 ProductLocation 생성
        ProductLocation bizLoc = buildLocation("서울특별시 서초구", 37.4830, 127.0320);

        User dummyBusinessUser = User.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .userAccount("businessuser")
                .userPw(encodedPw)
                .email("businessuser@example.com")
                .profileImage("https://example.com/business.png")
                .role(Role.USER)
                .memberType(MemberType.BUSINESS)
                .user_location(bizLoc)              // ★ 여기로 변경
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        userRepository.save(dummyBusinessUser);
    }

    // ----------------------
    // 더미 카테고리
    // ----------------------
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

    // ----------------------
    // 더미 일반 상품
    // ----------------------
    private void createDummyProducts() {
        User user = userRepository.findById(
                UUID.fromString("ffd9c396-b70e-4d59-8d04-fad7b1fa1df2")
        ).orElseThrow();

        Category cat1 = categoryRepository.findById(1).orElseThrow();
        Category cat2 = categoryRepository.findById(2).orElseThrow();

        List<ProductLocation> locations = new ArrayList<>();
        locations.add(buildLocation("서울특별시 강남구 예시동 1", 37.4970, 127.0270));
        locations.add(buildLocation("서울특별시 강남구 예시동 2", 37.4980, 127.0280));
        locations.add(buildLocation("서울특별시 강남구 예시동 3", 37.4990, 127.0290));

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

    // ----------------------
    // 더미 경매 상품
    // ----------------------
    private void createDummyAuctions() {
        User business = userRepository.findById(
                UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        ).orElseThrow();

        Category cat1 = categoryRepository.findById(1).orElseThrow();
        Category cat2 = categoryRepository.findById(3).orElseThrow();

        List<ProductLocation> auctionLocations = new ArrayList<>();
        auctionLocations.add(buildLocation("서울특별시 서초구 예시동 A", 37.4830, 127.0320));
        auctionLocations.add(buildLocation("서울특별시 서초구 예시동 B", 37.4840, 127.0330));
        auctionLocations.add(buildLocation("서울특별시 서초구 예시동 C", 37.4850, 127.0340));

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
                            .main_Image(true)
                            .savedTime(LocalDateTime.now())
                            .build(),
                    ItemImage.builder()
                            .item(auction)
                            .imageUrl("https://example.com/auction" + (2 * i) + ".jpg")
                            .main_Image(false)
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
