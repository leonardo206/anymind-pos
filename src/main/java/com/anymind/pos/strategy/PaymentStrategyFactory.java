package com.anymind.pos.strategy;

import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.anymind.pos.type.PaymentMethodType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethodType, PaymentStrategy> strategies;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategies = strategies.stream().collect(Collectors.toMap(PaymentStrategy::getPaymentMethod, Function.identity()));
    }

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        PaymentStrategy strategy = strategies.get(paymentRequest.paymentMethod());
        if (strategy == null) {
            throw new InvalidPaymentRequestException("Unsupported payment method: " + paymentRequest.paymentMethod());
        }
        return strategy.processPayment(paymentRequest);
    }

}
