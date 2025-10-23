package org.example.trusttrade.global.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.client.TossPaymentClient;
import org.example.trusttrade.global.domain.order.Order;
import org.example.trusttrade.global.dto.ConfirmPaymentRequest;
import org.example.trusttrade.global.dto.OrderPaymentResDto;
import org.example.trusttrade.global.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final TossPaymentClient tossPaymentClient;
    private final NotificationService notificationService;

    //결제 정보 검증
    public Order verifyPayment(OrderPaymentResDto orderPaymentResDto) {
        String orderId = orderPaymentResDto.getOrderId();
        Order find = orderRepository.findById(orderId).orElse(null);

        if (find.getAmount() == orderPaymentResDto.getAmount()) {
            return find;
        } else {
            return null;
        }
    }

    //결제 정보 인증 api 호출
    public void confirmAndSavePayment(ConfirmPaymentRequest request) throws IOException, InterruptedException {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        try {
            HttpResponse<String> response = tossPaymentClient.requestConfirm(request);
            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode == 200) {
                //payment키 설정
                order.setPaymentKey(request.getPaymentKey());
                // 주문 조회 및 상태 변경
                processOrderAndPayment(request.getOrderId(), true); // 결제 성공 시 처리
                //알림 처리
                notificationService.createNotification(
                        "결제가 완료되었습니다. 배송을 시작해주세요.", order.getSeller().getId());
                notificationService.createNotification(
                        "결제가 완료되었습니다.", order.getBuyer().getId());

            } else {
                // Toss 응답 실패 (ex. 결제 키 오류, 금액 불일치 등)
                //알림처리
                notificationService.createNotification(
                        "결제 실패하였습니다. 다시 결제를 시도해주세요", order.getBuyer().getId());
                //상태 변경
                processOrderAndPayment(request.getOrderId(), false);
                throw new IllegalStateException("결제 승인 실패: " + responseBody);
            }
        } catch (Exception e) {
            // DB 처리 실패 → 결제 취소 요청
            //취소 중 에러 처리
            log.error("결제 처리 중 예외 발생: {}", e.getMessage(), e);
            try {
                HttpResponse cancelResponse = tossPaymentClient.requestPaymentCancel(
                        request.getPaymentKey(),
                        "결제 승인 후 DB 저장 실패로 인한 자동 취소");
            }catch (Exception cancelEx) {
                log.error("결제 취소 요청 실패: {}", cancelEx.getMessage(), cancelEx);
            }
            //상태 변경
            processOrderAndPayment(request.getOrderId(), false);
            //알림 처리
            notificationService.createNotification(
                    "결제 정보 저장에 실패하였습니다. 다시 결제를 시도해주세요", order.getBuyer().getId());
        }

    }


    // 주문과 결제 상태 변경을 처리하는 메서드
    private void processOrderAndPayment(String orderId, boolean isSuccess) {
        try {
            // 주문 조회 및 상태 변경 >> 테스트용 주문 id 따로 설정해서 사용해야함
            Order order = orderRepository.findById(orderId).orElse(null);
            if (isSuccess) {
                order.paidOrder();  // 결제 성공 시 주문 상태 변경
            } else {
                order.cancel();  // 결제 실패 시 주문 상태 변경
            }
            orderRepository.save(order);

        } catch (Exception ex) {
            throw new IllegalStateException("주문 또는 결제 상태 변경 중 오류 발생: " + ex.getMessage(), ex);
        }
    }

    //주문 취소 및 상태 변경(환불 시 사용)
    public Order cancelOrder(String orderId, String paymentKey, String reason) throws IOException, InterruptedException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문 정보가 존재하지 않습니다."));

        HttpResponse response = tossPaymentClient.requestPaymentCancel(paymentKey, reason);

        if (response.statusCode() == 200) {
            processOrderAndPayment(orderId, false);
            return orderRepository.findById(orderId).orElse(null);
        } else {
            throw new IllegalStateException("주문 취소를 실패하였습니다. 다시 시도해주세요 : " + response.body());
        }

    }
}
