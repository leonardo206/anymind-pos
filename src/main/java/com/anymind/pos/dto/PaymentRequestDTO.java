package com.anymind.pos.dto;

import com.anymind.pos.config.serialization.PaymentMethodDeserializer;
import com.anymind.pos.config.serialization.PriceDeserializer;
import com.anymind.pos.config.serialization.PriceSerializer;
import com.anymind.pos.type.PaymentMethodType;
import com.anymind.pos.validator.ValidPrice;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestDTO(@NotBlank(message = "CustomerId cant be null or empty") String customerId,
                                @ValidPrice
                                @JsonSerialize(using = PriceSerializer.class)
                                @JsonDeserialize(using = PriceDeserializer.class)
                                BigDecimal price,
                                @ValidPrice
                                @JsonSerialize(using = PriceSerializer.class)
                                @JsonDeserialize(using = PriceDeserializer.class)
                                BigDecimal priceModifier,
                                @NotNull(message = "Payment Method cant be null")
                                @JsonDeserialize(using = PaymentMethodDeserializer.class)
                                PaymentMethodType paymentMethod,
                                @NotNull(message = "Date cant be null") LocalDateTime datetime,
                                @Valid AdditionalItemDTO additionalItem,
                                @NotBlank(message = "Idempotency key cant be null or empty") String idempotencyKey
) {
}
