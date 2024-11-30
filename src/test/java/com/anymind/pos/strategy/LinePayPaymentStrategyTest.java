package com.anymind.pos.strategy;

import com.anymind.pos.domain.Payment;
import com.anymind.pos.domain.PaymentMethod;
import com.anymind.pos.dto.AdditionalItemDTO;
import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.anymind.pos.repository.PaymentMethodRepository;
import com.anymind.pos.repository.PaymentRepository;
import com.anymind.pos.type.PaymentMethodType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinePayPaymentStrategyTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private LinePayPaymentStrategy linePayPaymentStrategy;

    private final static UUID paymentMethodUUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(paymentMethodRepository.findByPaymentMethodType(PaymentMethodType.LINE_PAY))
            .thenReturn(Optional.of(
                    new PaymentMethod(
                            paymentMethodUUID,
                            PaymentMethodType.LINE_PAY,
                            BigDecimal.valueOf(0.8),
                            BigDecimal.valueOf(1.2),
                            BigDecimal.valueOf(0.1))));
        linePayPaymentStrategy.initializeModifiers();
    }

    @Test
    void testProcessPaymentSuccess() {
        PaymentRequestDTO request = new PaymentRequestDTO(
                "customer-id",
                new BigDecimal("100.00"),
                new BigDecimal("1.1"),
                PaymentMethodType.LINE_PAY,
                LocalDateTime.now(),
                new AdditionalItemDTO("","","",""),
                "key-123"
        );

        PaymentResponseDTO response = linePayPaymentStrategy.processPayment(request);

        assertNotNull(response);
        assertThat(new BigDecimal("110.00"), Matchers.comparesEqualTo(response.finalPrice())); // 100 * 1.1
        assertThat(new BigDecimal("10.00"), Matchers.comparesEqualTo(response.points())); // 100 * 1.1

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void testPriceModifierOutOfBounds() {
        PaymentRequestDTO request = new PaymentRequestDTO(
                "customer-id",
                new BigDecimal("100.00"),
                new BigDecimal("1.3"),
                PaymentMethodType.LINE_PAY,
                LocalDateTime.now(),
                new AdditionalItemDTO("","","",""),
                "key-123"
        );

        InvalidPaymentRequestException exception = assertThrows(InvalidPaymentRequestException.class, () -> {
            linePayPaymentStrategy.processPayment(request);
        });

        assertEquals("Price modifier is not within limits", exception.getMessage());
    }
}
