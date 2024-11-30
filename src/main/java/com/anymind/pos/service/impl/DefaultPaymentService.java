package com.anymind.pos.service.impl;

import com.anymind.pos.domain.Payment;
import com.anymind.pos.dto.*;
import com.anymind.pos.repository.PaymentRepository;
import com.anymind.pos.service.PaymentService;
import com.anymind.pos.strategy.PaymentStrategyFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final Executor taskExecutor;

    private final ConcurrentHashMap<String, CompletableFuture<PaymentResponseDTO>> processingMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public CompletableFuture<PaymentResponseDTO> executePayment(PaymentRequestDTO request) {
        String idempotencyKey = request.idempotencyKey();

        return processingMap.computeIfAbsent(idempotencyKey, key ->
                CompletableFuture.supplyAsync(() -> processPayment(request), taskExecutor)
                        .whenComplete((result, throwable) -> processingMap.remove(key))
        );
    }

    private PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        LOGGER.info("Received request to process payment for customer: [{}]", request.customerId());
        String idempotencyKey = request.idempotencyKey();

        Optional<Payment> existingPayment = paymentRepository.findPaymentByIdempotencyKey(idempotencyKey);
        if (existingPayment.isPresent()) {
            LOGGER.info("Duplicate payment request, returning existing payment");
            var existingPaymentEntity = existingPayment.get();
            return new PaymentResponseDTO(existingPaymentEntity.getFinalPrice(), existingPaymentEntity.getPoints());
        }

        return paymentStrategyFactory.processPayment(request);
    }

    @Override
    public SalesResponseDTO getSalesWithinDateRange(SalesReportRequestDTO request) {
        List<SalesRecordResponseDTO> results = paymentRepository.findHourlySales(request.startDateTime(),request.endDateTime());
        return new SalesResponseDTO(results);
    }

}
