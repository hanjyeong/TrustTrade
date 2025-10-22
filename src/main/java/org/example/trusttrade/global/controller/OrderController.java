package org.example.trusttrade.global.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.client.TossPaymentClient;
import org.example.trusttrade.global.domain.order.Order;
import org.example.trusttrade.global.dto.OrderCancelDto;
import org.example.trusttrade.global.dto.OrderPaymentResDto;
import org.example.trusttrade.global.dto.OrderReqDto;
import org.example.trusttrade.global.dto.OrderResDto;
import org.example.trusttrade.global.repository.OrderRepository;
import org.example.trusttrade.global.service.OrderService;
import org.example.trusttrade.global.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private final OrderService orderService;
    @Autowired
    private final TossPaymentClient tossPaymentClient;
    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;

    //order 생성
    @PostMapping("/new")
    public ResponseEntity<OrderPaymentResDto> createOrder(@RequestBody OrderReqDto request) {

        Order order = orderService.createOrder(request);
        order.getBuyer().getId().toString();


        //필수 설정 : 1.amount - currency, value 2.orderId 3.orderName
        OrderPaymentResDto response = new OrderPaymentResDto(
                order.getId(),
                order.getAmount(),
                order.getProductName());

        return ResponseEntity.ok(response);

    }

    //상품 수령시 order 상태 변경
    // 수령 완료 클릭 시 주문 상태를 COMPLETED로 변경
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable("orderId") String orderId) {
        try {
            orderService.completeOrder(orderId);
            return ResponseEntity.ok("주문 수령 완료 처리되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //구매 목록 조회 by userId
    @GetMapping("/{userId}/list")
    public ResponseEntity<?> getOrders(@PathVariable("userId") UUID userId) {

        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderResDto> response = orders.stream()
                .map(OrderResDto::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    //주문 취소
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody OrderCancelDto cancelReq) throws IOException, InterruptedException {

        try {
            paymentService.cancelOrder(cancelReq.getOrderId(), cancelReq.getPaymentKey(), cancelReq.getReason());
            return ResponseEntity.ok("결제 취소가 완료되었습니다.");

        }catch (EntityNotFoundException e) {
            // 사용자의 요청 데이터가 잘못된 경우 (예: 존재하지 않는 orderId)
            return ResponseEntity
                    .badRequest()
                    .body("요청 오류: " + e.getMessage());

        } catch (Exception e) {
            // 예상하지 못한 모든 예외
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다. 다시 시도해주세요.");
        }

    }


}
