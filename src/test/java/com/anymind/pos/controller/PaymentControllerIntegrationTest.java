package com.anymind.pos.controller;

import com.anymind.pos.dto.AdditionalItemDTO;
import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.service.impl.DefaultPaymentService;
import com.anymind.pos.type.PaymentMethodType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {

    public static final String CUSTOMER_ID = "customer-id";
    public static final String IDEMPOTENCY_KEY = "unique-key-1";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DefaultPaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRequestDTO validPaymentRequest;
    private PaymentResponseDTO validPaymentResponse;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        validPaymentRequest = new PaymentRequestDTO(
                CUSTOMER_ID,
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(1.0),
                PaymentMethodType.CASH,
                LocalDateTime.of(2024,1,1,10,0),
                new AdditionalItemDTO("","","",""),
                IDEMPOTENCY_KEY
        );

        validPaymentResponse = new PaymentResponseDTO(
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(5.00)
        );
    }

    @Test
    void testExecutePayment_Success() throws Exception {
        when(paymentService.executePayment(Mockito.any())).thenReturn(CompletableFuture.completedFuture(validPaymentResponse));

        MvcResult mvcResult = mockMvc.perform(post("/api/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalPrice", comparesEqualTo(validPaymentResponse.finalPrice().doubleValue())))
                .andExpect(jsonPath("$.points", comparesEqualTo(validPaymentResponse.points().doubleValue())));

    }

    @Test
    void testExecutePayment_ValidationError() throws Exception {
        PaymentRequestDTO invalidPaymentRequest = new PaymentRequestDTO(
                "customer-id",
                null,
                BigDecimal.valueOf(1.0),
                PaymentMethodType.CASH,
                LocalDateTime.of(2024,1,1,10,0),
                new AdditionalItemDTO("","","",""),
                "unique-key-1"
        );

        mockMvc.perform(post("/api/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidPaymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid request parameters")))
                .andExpect(jsonPath("$.details.price", is("Price cannot be null, zero, or negative")));
    }
}
