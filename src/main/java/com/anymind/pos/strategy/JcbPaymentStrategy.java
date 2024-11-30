package com.anymind.pos.strategy;

import com.anymind.pos.repository.PaymentMethodRepository;
import com.anymind.pos.repository.PaymentRepository;
import com.anymind.pos.type.PaymentMethodType;
import org.springframework.stereotype.Component;

@Component
public class JcbPaymentStrategy extends CreditCardPaymentStrategy {

    protected JcbPaymentStrategy(PaymentRepository paymentRepository, PaymentMethodRepository paymentMethodRepository) {
        super(paymentRepository, paymentMethodRepository);
    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.JCB;
    }
}