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
class JcbPaymentStrategyTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private JcbPaymentStrategy jcbPaymentStrategy;

    private final static UUID paymentMethodUUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(paymentMethodRepository.findByPaymentMethodType(PaymentMethodType.JCB)).thenReturn(
                Optional.of(new PaymentMethod(
                        paymentMethodUUID,
                        PaymentMethodType.JCB,
                        new BigDecimal("0.8"), // Min price modifier
                        new BigDecimal("1.2"), // Max price modifier
                        new BigDecimal("1.5")  // Points multiplier
                ))
        );
        jcbPaymentStrategy.initializeModifiers();
    }

    @Test
    void testProcessPayment_Successful() {
        PaymentRequestDTO request = new PaymentRequestDTO(
                "customer-id",
                new BigDecimal("100.00"),
                new BigDecimal("1.0"),
                PaymentMethodType.JCB,
                LocalDateTime.now(),
                new AdditionalItemDTO("1234","","",""),
                "key-123"
        );

        PaymentResponseDTO response = jcbPaymentStrategy.processPayment(request);

        assertNotNull(response);
        assertThat(new BigDecimal("100.00"), Matchers.comparesEqualTo(response.finalPrice()));
        assertThat(new BigDecimal("150.00"), Matchers.comparesEqualTo(response.points())); // 100 * 1.5

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_InvalidPriceModifier() {
        PaymentRequestDTO request = new PaymentRequestDTO(
                "customer-id",
                new BigDecimal("100.00"),
                new BigDecimal("0.5"), // Invalid modifier
                PaymentMethodType.JCB,
                LocalDateTime.now(),
                new AdditionalItemDTO("1234","","",""),
                "key-123"
        );

        assertThrows(InvalidPaymentRequestException.class, () -> jcbPaymentStrategy.processPayment(request));

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void testProcessPayment_MissingAdditionalItem() {
        PaymentRequestDTO request = new PaymentRequestDTO(
                "customer-id",
                new BigDecimal("100.00"),
                new BigDecimal("1.0"),
                PaymentMethodType.JCB,
                LocalDateTime.now(),
                null, // Missing additional item
                "key-123"
        );

        assertThrows(InvalidPaymentRequestException.class, () -> jcbPaymentStrategy.processPayment(request));

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void testProcessPayment_BlankLast4Digits() {
        PaymentRequestDTO request = new PaymentRequestDTO(
                "customer-id",
                new BigDecimal("100.00"),
                new BigDecimal("1.0"),
                PaymentMethodType.JCB,
                LocalDateTime.now(),
                new AdditionalItemDTO("","","",""), // Blank last 4 digits
                "key-123"
        );

        assertThrows(InvalidPaymentRequestException.class, () -> jcbPaymentStrategy.processPayment(request));

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void testGetPaymentMethod() {
        PaymentMethodType paymentMethodType = jcbPaymentStrategy.getPaymentMethod();

        assertEquals(PaymentMethodType.JCB, paymentMethodType);
    }
}
