package org.example.trusttrade.auction.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.auction.dto.DepositOrderPaymentResDto;
import org.example.trusttrade.auction.repository.DepositOrderRepository;
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
@RequestMapping("/depositPayments")
@RequiredArgsConstructor
public class DepositPaymentController {

    @Autowired
    private final DepositOrderService depositOrderService;
    @Autowired
    private final DepositOrderRepository depositOrderRepository;

    //보증금 결제 정보 검증
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody DepositOrderPaymentResDto depositOrderPaymentResDto) {

        depositOrderRepository.findById(depositOrderPaymentResDto.getDepositOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Deposit order not found"));

        if (depositOrderPaymentResDto.getAmount() != 20000) {
            return ResponseEntity.badRequest().body(PaymentErrorResponse.builder()
                    .code(400)
                    .message("결제 금액 정보가 일치하지 않습니다.")
                    .build());
        } else {
            return ResponseEntity.ok("결제 정보가 검증되었습니다.");
        }

    }

    //결제 인증 api 호출
    @PostMapping("/confirm")
    public ResponseEntity confirm(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        try {
            depositOrderService.confirmAndSavePayment(confirmPaymentRequest);
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
