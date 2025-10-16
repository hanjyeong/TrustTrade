package org.example.trusttrade.auction.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.auction.domain.Auction;
import org.example.trusttrade.auction.domain.DepositOrder;
import org.example.trusttrade.auction.dto.DepositOrderReqDto;
import org.example.trusttrade.auction.repository.AuctionRepository;
import org.example.trusttrade.auction.repository.DepositOrderRepository;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.repository.UserRepository;
import org.example.trusttrade.notification.service.NotificationService;
import org.example.trusttrade.order.client.TossPaymentClient;
import org.example.trusttrade.order.domain.Order;
import org.example.trusttrade.order.dto.ConfirmPaymentRequest;
import org.example.trusttrade.order.exception.OrderCancellationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;


@Service
@RequiredArgsConstructor
@Transactional
public class DepositOrderService {

    @Autowired
    private final AuctionRepository auctionRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final DepositOrderRepository depositOrderRepository;
    @Autowired
    private final TossPaymentClient tossPaymentClient;
    @Autowired
    private final NotificationService notificationService;

    //depositOrder 생성
    public DepositOrder createDepositOrder(DepositOrderReqDto request) {

        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));

        User bidder = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with id: " + request.getBuyerId()));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with id: " + request.getSellerId()));

        DepositOrder depositOrder = DepositOrder.create(auction, bidder, seller);
        depositOrderRepository.save(depositOrder);

        return depositOrder;
    }

    //결제 정보 인증 api 호출
    public void confirmAndSavePayment(ConfirmPaymentRequest request) throws IOException, InterruptedException {

        DepositOrder order = depositOrderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        HttpResponse<String> response = tossPaymentClient.requestConfirm(request);
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode == 200) {
            try {
                //payment키 설정
                order.setPaymentKey(request.getPaymentKey());
                // 주문 조회 및 상태 변경
                processOrderAndPayment(request.getOrderId(), true); // 결제 성공 시 처리
                //알림 처리
                notificationService.createNotification(
                        order.getAuctionName() + " 보증금 결제가 완료되었습니다.", order.getBidder().getId());

            } catch (Exception e) {
                //주문 취소
                HttpResponse cancelResponse = tossPaymentClient.requestPaymentCancel(
                        request.getPaymentKey(),
                        "결제 승인 후 DB 저장 실패로 인한 자동 취소");
                //상태 변경(paid -> cancle)
                processOrderAndPayment(request.getOrderId(), false);
                //알림 처리
                notificationService.createNotification(
                        "결제 정보 저장 실패로 결제가 최소되었습니다. 다시 결제를 시도해주세요", order.getBidder().getId());
                throw new OrderCancellationException("db 저장 실패로 결제 취소 " + cancelResponse.body());
            }

        } else {
            // Toss 응답 실패 (ex. 결제 키 오류, 금액 불일치 등)
            //알림처리
            notificationService.createNotification(
                    "결제 실패하였습니다. 다시 결제를 시도해주세요", order.getBidder().getId());
            throw new OrderCancellationException("결제 승인 실패: " + responseBody);
        }
    }


    // 주문과 결제 상태 변경을 처리하는 메서드
    private void processOrderAndPayment(String orderId, boolean isSuccess) {
        try {
            // 주문 조회 및 상태 변경 >> 테스트용 주문 id 따로 설정해서 사용해야함
            DepositOrder order = depositOrderRepository.findById(orderId).orElse(null);
            if (isSuccess) {
                order.paidDepositOrder();  // 결제 성공 시 주문 상태 변경
            } else {
                order.cancelDepositOrder();  // 결제 실패 시 주문 상태 변경
            }
            depositOrderRepository.save(order);

        } catch (Exception ex) {
            throw new IllegalStateException("주문 또는 결제 상태 변경 중 오류 발생: " + ex.getMessage(), ex);
        }
    }





    //보증금 결제 인증 api 호출
//    public void confirmAndSaveDepositPayment(ConfirmPaymentRequest request) {
//        DepositOrder depositOrder = depositOrderRepository.findById(request.getOrderId())
//                .orElseThrow(() -> new EntityNotFoundException("Deposit order not found"));
//
//        HttpResponse<String> response = tossPaymentClient.requestConfirm(request);
//        int statusCode = response.statusCode();
//        String body = response.body();
//
//        if(statusCode == 200) {
//            depositOrder.setPaymentKey(request.getPaymentKey());
//            processOrderAndPayment(request.getOrderId(), true);
//        }
//
//    }




    //결제 정보 인증 api 호출
//    public void confirmAndSavePayment(ConfirmPaymentRequest request) throws IOException, InterruptedException {
//
//        Order order = orderRepository.findById(request.getOrderId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
//
//        HttpResponse<String> response = tossPaymentClient.requestConfirm(request);
//        int statusCode = response.statusCode();
//        String responseBody = response.body();
//
//        if (statusCode == 200) {
//            try {
//                //payment키 설정
//                order.setPaymentKey(request.getPaymentKey());
//                // 주문 조회 및 상태 변경
//                processOrderAndPayment(request.getOrderId(), true); // 결제 성공 시 처리
//                //알림 처리
//                notificationService.createNotification(
//                        "결제가 완료되었습니다. 배송을 시작해주세요.", order.getSeller().getId());
//                notificationService.createNotification(
//                        "결제가 완료되었습니다.", order.getBuyer().getId());
//
//            } catch (OrderCancellationException e) {
//                //주문 취소
//                HttpResponse cancelResponse = tossPaymentClient.requestPaymentCancel(
//                        request.getPaymentKey(),
//                        "결제 승인 후 DB 저장 실패로 인한 자동 취소");
//                //상태 변경(paid -> cancle)
//                processOrderAndPayment(request.getOrderId(), false);
//                //알림 처리
//                notificationService.createNotification(
//                        "결제 정보 저장 실패로 결제가 최소되었습니다. 다시 결제를 시도해주세요", order.getBuyer().getId());
//                throw new OrderCancellationException("db 저장 실패로 결제 취소 " + cancelResponse.body());
//            }
//
//        } else {
//            // Toss 응답 실패 (ex. 결제 키 오류, 금액 불일치 등)
//            //알림처리
//            notificationService.createNotification(
//                    "결제 실패하였습니다. 다시 결제를 시도해주세요", order.getBuyer().getId());
//            throw new OrderCancellationException("결제 승인 실패: " + responseBody);
//        }
//    }


    // 주문과 결제 상태 변경을 처리하는 메서드
//    private void processOrderAndPayment(String orderId, boolean isSuccess) {
//        try {
//            // 주문 조회 및 상태 변경 >> 테스트용 주문 id 따로 설정해서 사용해야함
//            Order order = orderRepository.findById(orderId).orElse(null);
//            if (isSuccess) {
//                order.paidOrder();  // 결제 성공 시 주문 상태 변경
//            } else {
//                order.cancel();  // 결제 실패 시 주문 상태 변경
//            }
//            orderRepository.save(order);
//
//        } catch (Exception ex) {
//            throw new IllegalStateException("주문 또는 결제 상태 변경 중 오류 발생: " + ex.getMessage(), ex);
//        }
//    }

    //주문 취소 및 상태 변경(환불 시 사용)
//    public Order cancelOrder(String orderId, String paymentKey, String reason) throws IOException, InterruptedException {
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new EntityNotFoundException("해당 주문 정보가 존재하지 않습니다."));
//
//        HttpResponse response = tossPaymentClient.requestPaymentCancel(paymentKey, reason);
//
//        if (response.statusCode() == 200) {
//            processOrderAndPayment(orderId, false);
//            return orderRepository.findById(orderId).orElse(null);
//        } else {
//            throw new OrderCancellationException("주문 취소에 실패하였습니다. 다시 시도해주세요 : " + response.body());
//        }
//
//    }


}




