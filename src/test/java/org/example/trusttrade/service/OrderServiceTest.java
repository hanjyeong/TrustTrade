package org.example.trusttrade.service;

import org.example.trusttrade.global.service.OrderService;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.domain.products.ProductLocation;
import org.example.trusttrade.item.domain.products.ProductStatus;

import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.global.domain.order.Order;
import org.example.trusttrade.global.dto.OrderReqDto;
import org.example.trusttrade.login.service.UserService;
import org.example.trusttrade.global.repository.OrderRepository;

import org.example.trusttrade.item.repository.ProductLocationRepository;
import org.example.trusttrade.item.repository.ProductRepository;

import org.example.trusttrade.login.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductLocationRepository productLocationRepository;
    @Autowired
    private UserService userService;

    private LocalDateTime now = LocalDateTime.now();

    //주문 생성
    @Test
    public void createOrder() {

        //--------테스트용 유저, 상품, 주소 생성--------------
        //유저
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

        System.out.println("given 완료");

        Order order = orderService.createOrder(request);
        System.out.println("when 완료");

        Order findOrder = orderRepository.findById(order.getId()).orElse(null);
        assertEquals(order.getId(), findOrder.getId());
        System.out.println("then 완료");
    }

    //주문 상태 변경
    @Test
    public void completeOrder() {
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
        order.paidOrder();

        orderService.completeOrder(order.getId());
        System.out.println("when");

        Order findOrder = orderRepository.findById(order.getId()).orElse(null);
        if(findOrder.getStatus() != Order.Status.COMPLETED){
            System.out.println("변경 실패");
        }else {
            System.out.println("then");
        }

    }

    //주문 목록 조회
    @Test
    public void getOrdersByUserId() {
        //given
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
        System.out.println("given");


        List<Order> orders = orderService.getOrdersByUserId(order.getBuyer().getId());
        for(Order findOrder : orders){
            System.out.println("ProductName : " + findOrder.getProduct().getName());
        }
    }


}
