package org.example.trusttrade.auction.controller;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.auction.service.AuctionOrderService;
import org.example.trusttrade.auction.service.AuctionPaymentService;
import org.example.trusttrade.auction.service.DepositOrderService;
import org.example.trusttrade.order.dto.ConfirmPaymentRequest;
import org.example.trusttrade.order.dto.PaymentErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctionPayments")
@RequiredArgsConstructor
public class AuctionPaymentController {

    @Autowired
    private final AuctionPaymentService auctionPaymentService;

    //결제 인증 api 호출
    @PostMapping("/confirm")
    public ResponseEntity confirm(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        try {
            auctionPaymentService.confirmAndSavePayment(confirmPaymentRequest);
            return ResponseEntity.ok("결제 승인 및 저장 성공");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    PaymentErrorResponse.builder()
                            .code(400)
                            .message("결제 승인 중 에러 발생")
                            .build()
            );
        }
    }

}
