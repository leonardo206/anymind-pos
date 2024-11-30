package com.anymind.pos.config.serialization;

import com.anymind.pos.type.PaymentMethodType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PaymentMethodDeserializer extends JsonDeserializer<PaymentMethodType> {

    @Override
    public PaymentMethodType deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        return PaymentMethodType.fromString(value);
    }
}
