package com.anymind.pos.type;

import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethodType {
    CASH,
    CASH_ON_DELIVERY,
    VISA,
    MASTERCARD,
    AMEX,
    JCB,
    LINE_PAY,
    PAYPAY,
    POINTS,
    GRAB_PAY,
    BANK_TRANSFER,
    CHEQUE;


    public static PaymentMethodType fromString(String method) {
        try {
            return PaymentMethodType.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentRequestException("Invalid payment method: " + method);
        }
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
