package org.example.trusttrade.global.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
    private Long id;


    @OneToOne
    @JoinColumn(name = "order_id" ,nullable = false)
    private Order order;

    @Column(unique = true)
    private String paymentKey;

    private int amount;

    @Enumerated(EnumType.STRING)
    private Status status = Status.READY;

    @CreatedDate
    private LocalDateTime requestAt;
    @LastModifiedDate
    private LocalDateTime approvedAt;

    public enum Status{
        READY, SUCCESS, FAILED
    }

    public static Payment create(Order order, int amount) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);

        return payment;
    }

    //결제 성공
    public void successPayment(){
        if(this.status != Status.READY){
            throw new IllegalStateException("READY 상태가 아니면 결제 성공 처리가 불가능 합니다.");
        }
        this.status = Status.SUCCESS;
    }

    //결제 실패
    public void failedPayment(){
        if(this.status != Status.READY){
            throw new IllegalStateException("READY 상태가 아니면 결제 실패 처리가 불가능 합니다.");
        }
        this.status = Status.FAILED;
    }

}

