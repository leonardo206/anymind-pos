package com.anymind.pos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesRecordResponseDTO(@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC") LocalDateTime datetime,
                                     BigDecimal sales,
                                     BigDecimal points) {
}
