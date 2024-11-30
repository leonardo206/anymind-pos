package com.anymind.pos.strategy;

import com.anymind.pos.domain.AdditionalItem;
import com.anymind.pos.dto.AdditionalItemDTO;
import com.anymind.pos.repository.PaymentMethodRepository;
import com.anymind.pos.repository.PaymentRepository;
import com.anymind.pos.type.PaymentMethodType;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentStrategy extends BasePaymentStrategy {

    protected CashPaymentStrategy(PaymentRepository paymentRepository,
                                  PaymentMethodRepository paymentMethodRepository) {
        super(paymentRepository, paymentMethodRepository);
    }

    @Override
    protected AdditionalItem addAdditionalItem(AdditionalItemDTO additionalItemDTO) {
        return AdditionalItem.builder()
                .build();
    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.CASH;
    }
}
