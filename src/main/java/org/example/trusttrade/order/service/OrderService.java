package org.example.trusttrade.order.service;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.item.repository.ProductRepository;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.repository.UserRepository;
import org.example.trusttrade.order.domain.Order;
import org.example.trusttrade.order.dto.OrderReqDto;
import org.example.trusttrade.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    //order 생성
    public Order createOrder(OrderReqDto request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + request.getProductId()));

        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found with id: " + request.getBuyerId()));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with id: " + request.getSellerId()));


        Order order = Order.create(product, buyer, seller);
        orderRepository.save(order);

        return order;
    }

    //주문 수령 완료
    public void completeOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.completeOrder(); // 엔티티에서 상태 변경 메서드 호출

        // DB에 상태 변경 반영
        orderRepository.save(order);
    }


    //주문 목록 조회
    public List<Order> getOrdersByUserId(UUID userId){
        return orderRepository.getOrdersByUserId(userId);
    }


}
