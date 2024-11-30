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
public class BankTransferPaymentStrategy extends BasePaymentStrategy {

    protected BankTransferPaymentStrategy(PaymentRepository paymentRepository,
                                          PaymentMethodRepository paymentMethodRepository) {
        super(paymentRepository, paymentMethodRepository);
    }

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        validateBankAccountNumber(request.additionalItem());
        return super.processPayment(request);
    }

    @Override
    protected AdditionalItem addAdditionalItem(AdditionalItemDTO additionalItemDTO) {
        return AdditionalItem.builder()
                .bankAccount(additionalItemDTO.bankAccount())
                .build();
    }

    private void validateBankAccountNumber(AdditionalItemDTO additionalItemDTO) {
        if (Objects.isNull(additionalItemDTO)) {
            throw new InvalidPaymentRequestException("Additional item required");
        }
        if (additionalItemDTO.bankAccount().isBlank()) {
            throw new InvalidPaymentRequestException("Bank account is required");
        }
        if (additionalItemDTO.bankAccount().length() < 8 || additionalItemDTO.bankAccount().length() > 12) {
            throw new InvalidPaymentRequestException("Wrong bank account number");
        }

    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.BANK_TRANSFER;
    }
}
