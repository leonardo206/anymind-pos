package com.anymind.pos.config.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PriceSerializerTest {

    private final PriceSerializer serializer = new PriceSerializer();

    @Test
    void testSerialize_ValidValue() throws IOException {
        JsonGenerator generator = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);

        BigDecimal value = new BigDecimal("123.456");

        serializer.serialize(value, generator, provider);

        verify(generator).writeNumber(new BigDecimal("123.46"));
    }

    @Test
    void testSerialize_NullValue() throws IOException {
        JsonGenerator generator = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);

        serializer.serialize(null, generator, provider);

        verify(generator).writeNull();
    }

    @Test
    void testSerialize_ExactScaleValue() throws IOException {
        JsonGenerator generator = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);

        BigDecimal value = new BigDecimal("100.00");

        serializer.serialize(value, generator, provider);

        verify(generator).writeNumber(new BigDecimal("100.00"));
    }
}
