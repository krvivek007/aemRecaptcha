package com.vivek.captcha.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.SneakyThrows;

public final class JacksonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private JacksonUtils() {
        throw new UnsupportedOperationException("Instantiating object of this class is not allowed");
    }

   public static <T> String getJsonAsString(final T obj) {
        return getJsonAsString(obj, OBJECT_MAPPER);
    }

    public static <T> T getObjectFrom(final String jsonString, final Class<T> klaaz) {
        return getObjectFrom(jsonString, klaaz, OBJECT_MAPPER);
    }

    /**
     * This method returns object serialized into JSON format as a string value.
     *
     * @param obj          the {@link T}
     * @param <T>          the {@link T}
     * @param objectMapper the {@link ObjectMapper}
     * @return the {@link String}
     */
    @SneakyThrows
    public static <T> String getJsonAsString(final T obj, final ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T getObjectFrom(final String json,
                                      final Class<T> klaaz,
                                      final ObjectMapper objectMapper) {
        return objectMapper.readValue(json, klaaz);
    }
}

