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

    // 지도 API 테스트용
    private double[] offsetLatLng(double lat, double lng, double metersNorth, double metersEast) {
        double metersPerDegLat = 111_320d;
        double metersPerDegLng = 111_320d * Math.cos(Math.toRadians(lat));

        double dLat = metersNorth / metersPerDegLat;
        double dLng = metersEast  / metersPerDegLng;

        return new double[]{ lat + dLat, lng + dLng };
    }

    /**
     * 지도 반경(예: 5km) 테스트용으로 중심점 근처에 상품 4개를 심는다.
     * - P-A: +300m (반경 내)
     * - P-B: +1,500m (반경 내)
     * - P-C: +4,800m (반경 내, 경계 근처)
     * - P-D: +5,200m (반경 밖) → 5km로 조회 시 제외되어야 함
     */
    private void createMapTestProducts() {
        // 1) 기준 유저, 카테고리 로드
        User user = userRepository.findById(
                UUID.fromString("ffd9c396-b70e-4d59-8d04-fad7b1fa1df2")
        ).orElseThrow();

        // 카테고리 1,2를 사용 (네가 이미 만들어 둔 전자제품/주방용품)
        Category cat1 = categoryRepository.findById(1).orElseThrow();
        Category cat2 = categoryRepository.findById(2).orElseThrow();

        // 2) 중심점(강남역 근처 예시) — 프론트에서 지도를 이 좌표에 맞추고 테스트하면 편함
        final double centerLat = 37.4979;
        final double centerLng = 127.0276;

        // 3) 각 거리 케이스의 좌표 계산 (동쪽으로만 이동시켜 직관적 비교)
        double[] near300   = offsetLatLng(centerLat, centerLng, 0,  300);   // +300m 동쪽
        double[] near1500  = offsetLatLng(centerLat, centerLng, 0,  1500);  // +1.5km 동쪽
        double[] near4800  = offsetLatLng(centerLat, centerLng, 0,  4800);  // +4.8km 동쪽
        double[] out5200   = offsetLatLng(centerLat, centerLng, 0,  5200);  // +5.2km 동쪽 (반경 밖)

        // 4) ProductLocation 생성
        ProductLocation locA = ProductLocation.builder()
                .address("강남역 동쪽 300m 지점")
                .latitude(near300[0])
                .longitude(near300[1])
                .build();

        ProductLocation locB = ProductLocation.builder()
                .address("강남역 동쪽 1.5km 지점")
                .latitude(near1500[0])
                .longitude(near1500[1])
                .build();

        ProductLocation locC = ProductLocation.builder()
                .address("강남역 동쪽 4.8km 지점")
                .latitude(near4800[0])
                .longitude(near4800[1])
                .build();

        ProductLocation locD = ProductLocation.builder()
                .address("강남역 동쪽 5.2km 지점 (반경 밖)")
                .latitude(out5200[0])
                .longitude(out5200[1])
                .build();

        // 5) Product 생성 및 저장 (이미지/카테고리 매핑 포함)
        saveProductWithImagesAndCategories(user, "지도테스트-A(300m)", "반경 5km 내 - 매우 가까움", 110000, locA, cat1, cat2);
        saveProductWithImagesAndCategories(user, "지도테스트-B(1.5km)", "반경 5km 내 - 가까움",   120000, locB, cat1, cat2);
        saveProductWithImagesAndCategories(user, "지도테스트-C(4.8km)", "반경 5km 내 - 경계부근", 130000, locC, cat1, cat2);
        saveProductWithImagesAndCategories(user, "지도테스트-D(5.2km)", "반경 5km 밖 - 제외대상", 140000, locD, cat1, cat2);
    }

    /**
     * 공통 저장 헬퍼: 상품 + 이미지 2장 + 카테고리 2개 매핑
     */
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



    private void createDummyUser() {
        String encodedPw ="1234"; // 더미 계정 비밀번호: 1234
        User dummyUser = User.builder()
                .id(UUID.fromString("ffd9c396-b70e-4d59-8d04-fad7b1fa1df2"))
                .userAccount("dummyuser")
                .userPw(encodedPw)
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
        String encodedPw = "1234"; // 더미 비즈니스 계정 비밀번호: 1234
        User dummyBusinessUser = User.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .userAccount("businessuser")
                .userPw(encodedPw)
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
