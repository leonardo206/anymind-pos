package com.anymind.pos.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SalesReportRequestDTO(@NotNull(message = "startDateTime cant be null") LocalDateTime startDateTime,
                                    @NotNull(message = "endDateTime cant be null") LocalDateTime endDateTime) {
}
