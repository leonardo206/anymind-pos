package com.anymind.pos.strategy;

import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.type.PaymentMethodType;

public interface PaymentStrategy {
    PaymentResponseDTO processPayment(PaymentRequestDTO request);
    PaymentMethodType getPaymentMethod();
}
