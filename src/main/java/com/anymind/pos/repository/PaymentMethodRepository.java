package com.anymind.pos.repository;

import com.anymind.pos.domain.PaymentMethod;
import com.anymind.pos.type.PaymentMethodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    Optional<PaymentMethod> findByPaymentMethodType(PaymentMethodType paymentMethodType);
}
