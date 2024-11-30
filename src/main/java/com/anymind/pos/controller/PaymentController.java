package com.anymind.pos.controller;

import com.anymind.pos.dto.PaymentRequestDTO;
import com.anymind.pos.dto.PaymentResponseDTO;
import com.anymind.pos.dto.SalesReportRequestDTO;
import com.anymind.pos.dto.SalesResponseDTO;
import com.anymind.pos.service.impl.DefaultPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final DefaultPaymentService paymentService;

    @PostMapping
    public CompletableFuture<ResponseEntity<PaymentResponseDTO>> executePayment(@RequestBody @Valid PaymentRequestDTO request) {
        return paymentService.executePayment(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/sales")
    public SalesResponseDTO getSales(@RequestBody @Valid SalesReportRequestDTO request) {
        return paymentService.getSalesWithinDateRange(request);
    }

}
