package com.anymind.pos.strategy;

import com.anymind.pos.domain.AdditionalItem;
import com.anymind.pos.dto.AdditionalItemDTO;
import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.anymind.pos.repository.PaymentMethodRepository;
import com.anymind.pos.repository.PaymentRepository;

import java.util.Objects;

public abstract class CreditCardPaymentStrategy extends BasePaymentStrategy {


    protected CreditCardPaymentStrategy(PaymentRepository paymentRepository,
                                        PaymentMethodRepository paymentMethodRepository) {
        super(paymentRepository, paymentMethodRepository);
    }

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        validateCreditCardNumber(request.additionalItem());
        return super.processPayment(request);
    }

    @Override
    protected AdditionalItem addAdditionalItem(AdditionalItemDTO additionalItemDTO) {
        return AdditionalItem.builder()
                .last4(additionalItemDTO.last4())
                .build();
    }

    private void validateCreditCardNumber(AdditionalItemDTO additionalItemDTO) {
        if (Objects.isNull(additionalItemDTO)) {
            throw new InvalidPaymentRequestException("Additional item required");
        }
        if (additionalItemDTO.last4().isBlank()) {
            throw new InvalidPaymentRequestException("Card last 4 digit is required");
        }
        if (additionalItemDTO.last4().length() != 4) {
            throw new InvalidPaymentRequestException("Wrong number of digit");
        }

    }
}
