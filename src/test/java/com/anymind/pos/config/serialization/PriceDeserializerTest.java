package com.anymind.pos.config.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PriceDeserializerTest {

    private final PriceDeserializer deserializer = new PriceDeserializer();

    @Test
    void testDeserialize_ValidValue() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        String validValue = "123.456";
        when(parser.getText()).thenReturn(validValue);

        BigDecimal result = deserializer.deserialize(parser, context);

        assertEquals(new BigDecimal("123.46"), result);
    }

    @Test
    void testDeserialize_EmptyValue() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        String emptyValue = "";
        when(parser.getText()).thenReturn(emptyValue);

        BigDecimal result = deserializer.deserialize(parser, context);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testDeserialize_InvalidValue() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        String invalidValue = "invalid";
        when(parser.getText()).thenReturn(invalidValue);

        assertThrows(NumberFormatException.class, () -> deserializer.deserialize(parser, context));
    }

    @Test
    void testDeserialize_ValueWithWhitespace() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        String valueWithWhitespace = "   100.00   ";
        when(parser.getText()).thenReturn(valueWithWhitespace);

        BigDecimal result = deserializer.deserialize(parser, context);

        assertEquals(new BigDecimal("100.00"), result);
    }
}

