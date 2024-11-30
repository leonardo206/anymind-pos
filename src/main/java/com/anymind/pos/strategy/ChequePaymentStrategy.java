package com.anymind.pos.strategy;

import com.anymind.pos.domain.AdditionalItem;
import com.anymind.pos.dto.AdditionalItemDTO;
import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.anymind.pos.repository.PaymentMethodRepository;
import com.anymind.pos.repository.PaymentRepository;
import com.anymind.pos.type.PaymentMethodType;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ChequePaymentStrategy extends BasePaymentStrategy {

    protected ChequePaymentStrategy(PaymentRepository paymentRepository,
                                    PaymentMethodRepository paymentMethodRepository) {
        super(paymentRepository, paymentMethodRepository);
    }

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        validateChequeNumber(request.additionalItem());
        return super.processPayment(request);
    }

    @Override
    protected AdditionalItem addAdditionalItem(AdditionalItemDTO additionalItemDTO) {
        return AdditionalItem.builder()
                .chequeNumber(additionalItemDTO.chequeNumber())
                .build();
    }

    private void validateChequeNumber(AdditionalItemDTO additionalItemDTO) {
        if (Objects.isNull(additionalItemDTO)) {
            throw new InvalidPaymentRequestException("Additional item required");
        }
        if (additionalItemDTO.chequeNumber().isBlank()) {
            throw new InvalidPaymentRequestException("Cheque number is required");
        }
        if (additionalItemDTO.chequeNumber().length() != 6) {
            throw new InvalidPaymentRequestException("Wrong cheque number digit");
        }

    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.CHEQUE;
    }
}
