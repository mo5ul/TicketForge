package com.mo5ul.ticketforge.payment;
public record OrderResponse(String orderId,long amountPaise, String currency, String keyId){}