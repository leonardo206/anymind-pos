package com.anymind.pos.config.serialization;

import com.anymind.pos.exception.InvalidPaymentRequestException;
import com.anymind.pos.type.PaymentMethodType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentMethodDeserializerTest {

    private final PaymentMethodDeserializer deserializer = new PaymentMethodDeserializer();

    @Test
    void testDeserialize_ValidValue() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        String validValue = "MASTERCARD";
        when(parser.getText()).thenReturn(validValue);

        PaymentMethodType result = deserializer.deserialize(parser, context);

        assertEquals(PaymentMethodType.MASTERCARD, result);
    }

    @Test
    void testDeserialize_InvalidValue() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        String invalidValue = "INVALID_METHOD";
        when(parser.getText()).thenReturn(invalidValue);

        assertThrows(InvalidPaymentRequestException.class, () -> deserializer.deserialize(parser, context));
    }
}
