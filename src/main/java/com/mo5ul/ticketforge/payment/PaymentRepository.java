package com.mo5ul.ticketforge.payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentRepository extends JpaRepository<Payment, Long>{
    Optional<Payment> findByProviderOrderId(String providerOrderId);
}