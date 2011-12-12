/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.io.Serializable;

/**
 * Factory interface for providing Converters based on a source and target type.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 * 
 */
public interface ConverterFactory extends Serializable {
    <SOURCE, TARGET> Converter<SOURCE, TARGET> createConverter(
            Class<SOURCE> sourceType, Class<TARGET> targetType);

}
