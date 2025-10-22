package org.example.trusttrade.service;

import org.example.trusttrade.global.service.OrderService;
import org.example.trusttrade.global.service.PaymentService;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.domain.products.ProductLocation;
import org.example.trusttrade.item.domain.products.ProductStatus;

import org.example.trusttrade.login.domain.User;

import org.example.trusttrade.global.domain.order.Order;
import org.example.trusttrade.item.dto.request.BasicItemDto;
import org.example.trusttrade.global.dto.OrderPaymentResDto;
import org.example.trusttrade.global.dto.OrderReqDto;

import org.example.trusttrade.item.repository.ProductLocationRepository;
import org.example.trusttrade.item.repository.ProductRepository;

import org.example.trusttrade.login.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class PaymentServiceTest {

    LocalDateTime now = LocalDateTime.now();

    @Autowired private PaymentService paymentService;
    @Autowired private OrderService orderService;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductLocationRepository productLocationRepository;
    @Autowired private UserRepository userRepository;

    //결제 정보 검증
    @Test
    @Rollback(false)
    public void verifyOrder(){

        User user1 = User.builder()
                .id(UUID.randomUUID())
                .email("hyunjiin@google.com")
                .profileImage("img")
                .role(User.Role.USER)
                .memberType(User.MemberType.BUSINESS)
                .businessNumber("02-123-1234")
                .createdAt(LocalDateTime.now())
                .roughAddress("suwonsi")
                .build();

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("hyunjin2@google.com")
                .profileImage("img2")
                .role(User.Role.USER)
                .memberType(User.MemberType.BUSINESS)
                .businessNumber("02-123-12345")
                .createdAt(LocalDateTime.now())
                .roughAddress("suwonsi")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        //주소
        ProductLocation location1 = ProductLocation.builder()
                .latitude(5.5)
                .longitude(3.2)
                .address("**상가 정문 앞")
                .build();
        productLocationRepository.save(location1);

        //상품
        Product product1 = Product.builder()
                .user(user1)
                .name("원목 의자 20개")
                .description("컬러 : 카라멜, 사용기한 : 10개월, 추가 : 사정에 의한 폐업으로 급하게 재고처리합니다.")
                .productLocation(location1)
                .createdTime(now)
                .productPrice(500000)
                .status(ProductStatus.SALE)
                .build();
        productRepository.save(product1);

        //주문 생성
        OrderReqDto request = new OrderReqDto();
        request.setProductId(product1.getId());
        request.setBuyerId(user2.getId());
        request.setSellerId(user1.getId());
        Order order = orderService.createOrder(request);
        System.out.println("given 완료");

        OrderPaymentResDto orderDto = new OrderPaymentResDto();
        orderDto.setOrderId(order.getId());
        orderDto.setAmount(500000);
        orderDto.setProductName("원목 의자 20개");
        System.out.println("when");

        paymentService.verifyPayment(orderDto);
        System.out.println("then");

    }


    //포스트맨 테스트용
    @Test
    @Rollback(false)
    public void postman(){
        User seller = User.builder()
                .id(UUID.randomUUID())
                .email("hyunjiin@google.com")
                .profileImage("img")
                .role(User.Role.USER)
                .memberType(User.MemberType.BUSINESS)
                .businessNumber("02-123-1234")
                .createdAt(LocalDateTime.now())
                .roughAddress("suwonsi")
                .build();

        User buyer = User.builder()
                .id(UUID.randomUUID())
                .email("hyunjin2@google.com")
                .profileImage("img2")
                .role(User.Role.USER)
                .memberType(User.MemberType.BUSINESS)
                .businessNumber("02-123-12345")
                .createdAt(LocalDateTime.now())
                .roughAddress("suwonsi")
                .build();

        userRepository.save(seller);
        userRepository.save(buyer);

        BasicItemDto dto = new BasicItemDto();
        // setter 또는 생성자를 통해 name, description, price, location 정보 설정
        dto.setTitle("책상");
        dto.setDescription("상태 좋은 책상 팝니다");
        dto.setPrice(1500);
        dto.setLatitude(37.1234);
        dto.setLongitude(127.1234);
        dto.setAddress("**상가 주소 앞");

        Product product = Product.fromDto(dto, seller);
        productRepository.save(product); // <- 여기서 item + product 테이블에 insert 됨

        System.out.println("product class: " + product.getClass());
        Product findProduct = productRepository.findById(product.getId()).orElse(null);
        System.out.println("findProduct: " + findProduct.getProductPrice());





    }
}
