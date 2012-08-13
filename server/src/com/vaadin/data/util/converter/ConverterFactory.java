/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.io.Serializable;

/**
 * Factory interface for providing Converters based on a presentation type and a
 * model type.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 * 
 */
public interface ConverterFactory extends Serializable {
    public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType);

}
