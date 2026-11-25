package com.mo5ul.ticketforge.payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
@Configuration 
public class RazorpayConfig{
    @Bean
    RazorpayClient razorpayClient(@Value("${app.razorpay.key-id}")String keyId, @Value ("${app.razorpay.key-secret}") String keySecret) throws RazorpayException{
        return new RazorpayClient(keyId, keySecret);
    }
}