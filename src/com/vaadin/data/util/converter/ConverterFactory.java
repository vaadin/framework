package com.vaadin.data.util.converter;

public interface ConverterFactory {
    <SOURCE, TARGET> Converter<SOURCE, TARGET> createConverter(
            Class<SOURCE> sourceType, Class<TARGET> targetType);

}
