package com.mo5ul.ticketforge.payment;
import java.time.Instant;
import jakarta.persistence.*;
@Entity 
@Table(name="payments")
public class Payment{
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="seat_id", nullable=false)
    private long seatId;
    @Column(name="user_id",nullable=false)
    private long userId;
    @Column (name="amount_paise", nullable=false)
    private long amountPaise;
    @Column(name="provider_order_id", nullable = false, unique=true)
    private String providerOrderId;
    @Column(name="provider_payment_id", unique=true)
    private String providerPaymentId;
    @Column(nullable=false)
    private String status="CREATED";
    @Column(name="paid_at")
    private Instant paidAt;
    protected Payment(){}
    public Payment(Long seatId, Long userId, long amountPaise, String providerOrderId){
        this.seatId=seatId;
        this.userId=userId;
        this.amountPaise=amountPaise;
        this.providerOrderId=providerOrderId;
    }
    public Long getId() {return id;}
    public Long getseatId(){return seatId;}
    public Long getUserId(){return userId;}
    public long getAmountPaise(){return amountPaise;}
    public String getProviderOrderId(){return providerOrderId;}
    public String getProviderPaymentId(){return providerPaymentId;}
    public String getStatus(){return status;}
    public Instant getPaidAt(){return paidAt;}
}