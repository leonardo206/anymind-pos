package com.anymind.pos.strategy;

import com.anymind.pos.domain.AdditionalItem;
import com.anymind.pos.dto.AdditionalItemDTO;
import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.anymind.pos.repository.PaymentMethodRepository;
import com.anymind.pos.repository.PaymentRepository;
import com.anymind.pos.type.CourierServiceType;
import com.anymind.pos.type.PaymentMethodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class CashOnDeliveryPaymentStrategy extends BasePaymentStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(CashOnDeliveryPaymentStrategy.class);

    private static final EnumSet<CourierServiceType> ALLOWED_COURIER_SERVICES = EnumSet.of(
            CourierServiceType.YAMATO,
            CourierServiceType.SAGAWA
    );

    protected CashOnDeliveryPaymentStrategy(PaymentRepository paymentRepository,
                                            PaymentMethodRepository paymentMethodRepository) {
        super(paymentRepository, paymentMethodRepository);
    }


    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        validateCourierService(request.additionalItem().courier());
        return super.processPayment(request);
    }

    @Override
    protected AdditionalItem addAdditionalItem(AdditionalItemDTO additionalItemDTO) {
        return AdditionalItem.builder()
                .courier(additionalItemDTO.courier())
                .build();
    }

    private void validateCourierService(String courierService) {
        if (courierService.isBlank()) {
            throw new InvalidPaymentRequestException("Courier service cannot be null or empty.");
        }
        try {
            CourierServiceType courierServiceType = CourierServiceType.fromString(courierService);
            if (!ALLOWED_COURIER_SERVICES.contains(courierServiceType)) {
                throw new InvalidPaymentRequestException(
                        String.format("Courier service '%s' is not allowed. Only YAMATO and SAGAWA are accepted.", courierService)
                );
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid courier service type: {}", courierService, e);
            throw new InvalidPaymentRequestException(
                    "Invalid courier type"
            );
        }
    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.CASH_ON_DELIVERY;
    }

}
