package com.anymind.pos.domain;

import com.anymind.pos.type.PaymentMethodType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;

    private BigDecimal priceModifierMin;
    private BigDecimal priceModifierMax;
    private BigDecimal pointsMultiplier;

}
