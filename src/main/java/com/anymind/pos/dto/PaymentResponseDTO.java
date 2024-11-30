package com.anymind.pos.dto;

import com.anymind.pos.config.serialization.PriceDeserializer;
import com.anymind.pos.config.serialization.PriceSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

public record PaymentResponseDTO(BigDecimal finalPrice,
                                 @JsonSerialize(using = PriceSerializer.class)
                                 @JsonDeserialize(using = PriceDeserializer.class)
                                 BigDecimal points) {
}
