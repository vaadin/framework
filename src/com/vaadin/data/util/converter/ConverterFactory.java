/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.io.Serializable;

public interface ConverterFactory extends Serializable {
    <SOURCE, TARGET> Converter<SOURCE, TARGET> createConverter(
            Class<SOURCE> sourceType, Class<TARGET> targetType);

}
