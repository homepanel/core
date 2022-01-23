package com.homepanel.core.serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CustomModule extends SimpleModule {

    public CustomModule() {
        super();
        addSerializer(LocalTime.class, new LocalTimeSerializer());
        addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        addSerializer(LocalDate.class, new LocalDateSerializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
    }
}