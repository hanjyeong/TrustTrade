package org.example.trusttrade.order.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.order.domain.Order;
import org.example.trusttrade.order.dto.ConfirmPaymentRequest;
import org.example.trusttrade.order.dto.OrderPaymentResDto;
import org.example.trusttrade.order.dto.PaymentErrorResponse;
import org.example.trusttrade.order.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


//    //결제 정보 검증
//    @PostMapping("/verify")
//    public ResponseEntity<?> verifyPayment(@RequestBody OrderPaymentResDto orderPaymentResDto) {
//
//        Order order = paymentService.verifyPayment(orderPaymentResDto);
//        if (order == null) {
//            return ResponseEntity.badRequest().body(PaymentErrorResponse.builder()
//                    .code(400)
//                    .message("결제 금액 정보가 유효하지 않습니다.")
//                    .build());
//        } else {
//            return ResponseEntity.ok("결제 정보가 검증되었습니다.");
//        }
//    }

    //결제 인증 api 호출
    @PostMapping("/confirm")
    public ResponseEntity confirm(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        try {

            paymentService.confirmAndSavePayment(confirmPaymentRequest);

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