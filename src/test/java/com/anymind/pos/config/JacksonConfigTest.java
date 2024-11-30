package com.anymind.pos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonConfigTest {

    private final JacksonConfig jacksonConfig = new JacksonConfig();

    @Test
    void testObjectMapper_CustomSerializersAndDeserializers() throws Exception {
        ObjectMapper objectMapper = jacksonConfig.objectMapper();

        BigDecimal value = new BigDecimal("123.456");
        String json = objectMapper.writeValueAsString(value);
        assertEquals("123.46", json);

        BigDecimal deserializedValue = objectMapper.readValue("123.456", BigDecimal.class);
        assertEquals(new BigDecimal("123.46"), deserializedValue);

        BigDecimal emptyValue = objectMapper.readValue("\"\"", BigDecimal.class);
        assertEquals(BigDecimal.ZERO, emptyValue);
    }
}

