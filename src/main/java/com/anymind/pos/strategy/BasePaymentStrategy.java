package com.anymind.pos.strategy;

import com.anymind.pos.domain.AdditionalItem;
import com.anymind.pos.domain.Payment;
import com.anymind.pos.dto.AdditionalItemDTO;
import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.anymind.pos.repository.PaymentMethodRepository;
import com.anymind.pos.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;

public abstract class BasePaymentStrategy implements PaymentStrategy {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    private BigDecimal minPriceModifier;

    private BigDecimal maxPriceModifier;

    private BigDecimal pointMultiplier;

    protected BasePaymentStrategy(PaymentRepository paymentRepository, PaymentMethodRepository paymentMethodRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @PostConstruct
    protected void initializeModifiers() {
        var paymentMethodOptional = paymentMethodRepository.findByPaymentMethodType(getPaymentMethod());
        if (paymentMethodOptional.isEmpty()) {
            throw new IllegalStateException("Payment method not found for " + getPaymentMethod());
        }
        var paymentMethod = paymentMethodOptional.get();
        this.minPriceModifier = paymentMethod.getPriceModifierMin();
        this.maxPriceModifier = paymentMethod.getPriceModifierMax();
        this.pointMultiplier = paymentMethod.getPointsMultiplier();
    }

    /**
     * Processes the payment request, applying discounts and calculating points.
     *
     * @param request the payment request
     * @return a {@link PaymentResponseDTO} with the results
     */
    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        // Assuming price and priceModifier current value correctness has been valuated in calling service
        validatePriceModifier(request.priceModifier());
        BigDecimal finalPrice = applyPriceModifier(request.price(), request.priceModifier());
        BigDecimal earnedPoints = calculatePoints(request.price());
        savePayment(request, finalPrice, earnedPoints);
        return new PaymentResponseDTO(finalPrice, earnedPoints);
    }

    protected abstract AdditionalItem addAdditionalItem(AdditionalItemDTO additionalItemDTO);

    private void savePayment(PaymentRequestDTO request, BigDecimal finalPrice, BigDecimal earnedPoints) {
        var payment = Payment.builder()
                .paymentMethod(request.paymentMethod())
                .price(request.price())
                .priceModifier(request.priceModifier())
                .finalPrice(finalPrice)
                .points(earnedPoints)
                .datetime(request.datetime())
                .idempotencyKey(request.idempotencyKey())
                .additionalItem(addAdditionalItem(request.additionalItem()))
                .build();
        paymentRepository.save(payment);
    }
    private void validatePriceModifier(BigDecimal priceModifier) {
        if (priceModifier.compareTo(minPriceModifier) < 0 || priceModifier.compareTo(maxPriceModifier) > 0) {
            throw new InvalidPaymentRequestException("Price modifier is not within limits");
        }
    }

    private BigDecimal applyPriceModifier(BigDecimal price, BigDecimal priceModifier) {
        return price.multiply(priceModifier);
    }

    private BigDecimal calculatePoints(BigDecimal price) {
        return price.multiply(pointMultiplier);
    }
}
