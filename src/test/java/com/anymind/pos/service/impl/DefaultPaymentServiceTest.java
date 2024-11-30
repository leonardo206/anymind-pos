package com.anymind.pos.service.impl;

import com.anymind.pos.domain.Payment;
import com.anymind.pos.dto.*;
import com.anymind.pos.repository.PaymentRepository;
import com.anymind.pos.strategy.PaymentStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultPaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    private Executor taskExecutor;

    @InjectMocks
    private DefaultPaymentService paymentService;

    @BeforeEach
    void setUp() {
        taskExecutor = Executors.newSingleThreadExecutor();
        paymentService = new DefaultPaymentService(paymentRepository, paymentStrategyFactory, taskExecutor);
    }

    @Test
    void testExecutePayment_SuccessfulProcessing() throws Exception {
        PaymentRequestDTO request = mock(PaymentRequestDTO.class);
        when(request.idempotencyKey()).thenReturn("unique-id");

        when(paymentRepository.findPaymentByIdempotencyKey("unique-id")).thenReturn(Optional.empty());

        PaymentResponseDTO response = new PaymentResponseDTO(new BigDecimal("100.00"), new BigDecimal("50.00"));
        when(paymentStrategyFactory.processPayment(request)).thenReturn(response);

        CompletableFuture<PaymentResponseDTO> futureResponse = paymentService.executePayment(request);
        PaymentResponseDTO result = futureResponse.get();

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.finalPrice());
        assertEquals(new BigDecimal("50.00"), result.points());

        verify(paymentRepository, times(1)).findPaymentByIdempotencyKey("unique-id");
        verify(paymentStrategyFactory, times(1)).processPayment(request);
    }

    @Test
    void testExecutePayment_DuplicateRequest() throws Exception {
        PaymentRequestDTO request = mock(PaymentRequestDTO.class);
        when(request.idempotencyKey()).thenReturn("duplicate-id");

        Payment existingPayment = new Payment();
        existingPayment.setFinalPrice(new BigDecimal("50.00"));
        existingPayment.setPoints(new BigDecimal("25.00"));
        when(paymentRepository.findPaymentByIdempotencyKey("duplicate-id")).thenReturn(Optional.of(existingPayment));

        CompletableFuture<PaymentResponseDTO> futureResponse = paymentService.executePayment(request);
        PaymentResponseDTO result = futureResponse.get();

        assertNotNull(result);
        assertEquals(new BigDecimal("50.00"), result.finalPrice());
        assertEquals(new BigDecimal("25.00"), result.points());

        verify(paymentRepository, times(1)).findPaymentByIdempotencyKey("duplicate-id");
        verifyNoInteractions(paymentStrategyFactory);
    }

    @Test
    void testExecutePayment_ExceptionDuringProcessing() throws Exception {
        PaymentRequestDTO request = mock(PaymentRequestDTO.class);
        when(request.idempotencyKey()).thenReturn("error-id");

        when(paymentRepository.findPaymentByIdempotencyKey("error-id")).thenReturn(Optional.empty());
        when(paymentStrategyFactory.processPayment(request)).thenThrow(new RuntimeException("Processing error"));

        CompletableFuture<PaymentResponseDTO> futureResponse = paymentService.executePayment(request);

        ExecutionException exception = assertThrows(ExecutionException.class, futureResponse::get);
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertEquals("Processing error", exception.getCause().getMessage());

        verify(paymentRepository, times(1)).findPaymentByIdempotencyKey("error-id");
        verify(paymentStrategyFactory, times(1)).processPayment(request);
    }

    @Test
    void testGetSalesWithinDateRangeReturnsSales() {
        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 11, 30, 23, 59);
        var request = new SalesReportRequestDTO(startDateTime, endDateTime);

        SalesRecordResponseDTO record1 = new SalesRecordResponseDTO(startDateTime, new BigDecimal("100.00"), new BigDecimal("1.00"));
        SalesRecordResponseDTO record2 = new SalesRecordResponseDTO(endDateTime, new BigDecimal("150.00"), new BigDecimal("2.00"));

        List<SalesRecordResponseDTO> mockSales = Arrays.asList(record1, record2);

        when(paymentRepository.findHourlySales(startDateTime, endDateTime)).thenReturn(mockSales);

        SalesResponseDTO response = paymentService.generateSalesReportWithinDateRange(request);

        assertNotNull(response);
        assertEquals(2, response.sales().size());

        assertEquals(new BigDecimal("100.00"), response.sales().get(0).sales());
        assertEquals(new BigDecimal("150.00"), response.sales().get(1).sales());
        assertEquals(new BigDecimal("1.00"), response.sales().get(0).points());
        assertEquals(new BigDecimal("2.00"), response.sales().get(1).points());
    }

    @Test
    void testGetSalesWithinDateRangeNoSales() {
        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 11, 30, 23, 59);
        var request = new SalesReportRequestDTO(startDateTime, endDateTime);

        List<SalesRecordResponseDTO> mockSales = List.of();
        when(paymentRepository.findHourlySales(startDateTime, endDateTime)).thenReturn(mockSales);

        SalesResponseDTO response = paymentService.generateSalesReportWithinDateRange(request);

        assertNotNull(response);
        assertTrue(response.sales().isEmpty());
    }


    @Test
    void testGetSalesWithinDateRangeInvalidDateRange() {
        LocalDateTime invalidStartDateTime = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime invalidEndDateTime = LocalDateTime.of(2024, 11, 1, 23, 59);
        SalesReportRequestDTO invalidRequest = new SalesReportRequestDTO(invalidStartDateTime, invalidEndDateTime);

        SalesResponseDTO response = paymentService.generateSalesReportWithinDateRange(invalidRequest);

        assertNotNull(response);
        assertTrue(response.sales().isEmpty());
    }

    @Test
    void testGetSalesWithinDateRangeException() {
        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 11, 30, 23, 59);
        var request = new SalesReportRequestDTO(startDateTime, endDateTime);

        when(paymentRepository.findHourlySales(startDateTime, endDateTime)).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            paymentService.generateSalesReportWithinDateRange(request);
        });

        assertEquals("Database error", thrown.getMessage());
    }
}
