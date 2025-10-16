package org.example.trusttrade.auction.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.auction.domain.DepositOrder;
import org.example.trusttrade.auction.dto.DepositOrderPaymentResDto;
import org.example.trusttrade.auction.dto.DepositOrderReqDto;
import org.example.trusttrade.auction.service.DepositOrderService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.order.domain.Order;
import org.example.trusttrade.order.dto.OrderPaymentResDto;
import org.example.trusttrade.order.dto.OrderReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/depositOrders")
@RequiredArgsConstructor
public class DepositOrderController {

    @Autowired
    private final DepositOrderService depositOrderService;

    @PostMapping("/new")
    public ResponseEntity<?> createDeposit(DepositOrderReqDto request) {

        DepositOrder depositOrder = depositOrderService.createDepositOrder(request);

        //orderId, amount, orderName 필수 설정
        DepositOrderPaymentResDto response = new DepositOrderPaymentResDto(
                depositOrder.getId(),
                depositOrder.getAmount(),
                depositOrder.getAuctionName());

        return ResponseEntity.ok(response);
    }

}
