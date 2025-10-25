package org.example.trusttrade.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.trusttrade.item.domain.products.Product;
import org.example.trusttrade.login.domain.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id" ,nullable = false)
    private Product product;

    private int amount;

    @Column(unique = true)
    private String paymentKey;

    @Column(nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Status{
        PENDING, PAID, COMPLETED, CANCELLED
    }

    //order 생성
    public static Order create(Product product, User buyer, User seller) {
        Order order = new Order();
        order.id = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        order.amount = product.getProductPrice();
        order.productName = product.getName();
        order.product = product;
        order.seller = seller;
        order.buyer = buyer;

        return order;
    }

    //결제 인증 api 호출후 paymentKey 설정
    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }


    //결제 완료 상태
    public void paidOrder() {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("PENDING 상태가 아니면 결제 완료 처리할 수 없습니다.");
        }
        this.status = Status.PAID;
    }
    // 수령 완료 상태로 변경
    public void completeOrder() {
        if (this.status != Status.PAID) {
            throw new IllegalStateException("결제 완료 상태가 아니면 수령 완료 처리할 수 없습니다.");
        }
        this.status = Status.COMPLETED;
    }

    //결제 취소될 경우 상태
    public void cancel() {
        if(this.status != Status.PAID) {
            throw new IllegalStateException("PAID 상태가 아니면 취소 처리가 불가능합니다.");
        }
        this.status = Status.CANCELLED;
    }


}