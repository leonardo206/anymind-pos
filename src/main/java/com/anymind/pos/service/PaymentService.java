package com.anymind.pos.service;

import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.dto.SalesReportRequestDTO;
import com.anymind.pos.dto.SalesResponseDTO;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {

    CompletableFuture<PaymentResponseDTO> executePayment(PaymentRequestDTO request);

    SalesResponseDTO generateSalesReportWithinDateRange(SalesReportRequestDTO salesReportRequestDTO);
}
