/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util.converter;

import java.io.Serializable;
import java.util.Locale;

import com.vaadin.server.VaadinSession;

public class ConverterUtil implements Serializable {

    /**
     * Finds a converter that can convert from the given presentation type to
     * the given model type and back. Uses the given application to find a
     * {@link ConverterFactory} or, if application is null, uses the
     * {@link VaadinSession#getCurrent()}.
     * 
     * @param <PRESENTATIONTYPE>
     *            The presentation type
     * @param <MODELTYPE>
     *            The model type
     * @param presentationType
     *            The presentation type
     * @param modelType
     *            The model type
     * @param session
     *            The session to use to find a ConverterFactory or null to use
     *            the current session
     * @return A Converter capable of converting between the given types or null
     *         if no converter was found
     */
    public static <PRESENTATIONTYPE, MODELTYPE> Converter<PRESENTATIONTYPE, MODELTYPE> getConverter(
            Class<PRESENTATIONTYPE> presentationType,
            Class<MODELTYPE> modelType, VaadinSession session) {
        Converter<PRESENTATIONTYPE, MODELTYPE> converter = null;
        if (session == null) {
            session = VaadinSession.getCurrent();
        }

        if (session != null) {
            ConverterFactory factory = session.getConverterFactory();
            converter = factory.createConverter(presentationType, modelType);
        }
        return converter;

    }

    /**
     * Convert the given value from the data source type to the UI type.
     * 
     * @param modelValue
     *            The model value to convert
     * @param presentationType
     *            The type of the presentation value
     * @param converter
     *            The converter to (try to) use
     * @param locale
     *            The locale to use for conversion
     * @param <PRESENTATIONTYPE>
     *            Presentation type
     * 
     * @return The converted value, compatible with the presentation type, or
     *         the original value if its type is compatible and no converter is
     *         set.
     * @throws Converter.ConversionException
     *             if there is no converter and the type is not compatible with
     *             the model type.
     */
    @SuppressWarnings("unchecked")
    public static <PRESENTATIONTYPE, MODELTYPE> PRESENTATIONTYPE convertFromModel(
            MODELTYPE modelValue,
            Class<? extends PRESENTATIONTYPE> presentationType,
            Converter<PRESENTATIONTYPE, MODELTYPE> converter, Locale locale)
            throws Converter.ConversionException {
        if (converter != null) {
            return converter.convertToPresentation(modelValue, locale);
        }
        if (modelValue == null) {
            return null;
        }

        if (presentationType.isAssignableFrom(modelValue.getClass())) {
            return (PRESENTATIONTYPE) modelValue;
        } else {
            throw new Converter.ConversionException(
                    "Unable to convert value of type "
                            + modelValue.getClass().getName()
                            + " to presentation type "
                            + presentationType
                            + ". No converter is set and the types are not compatible.");
        }
    }

    /**
     * @param <MODELTYPE>
     * @param <PRESENTATIONTYPE>
     * @param presentationValue
     * @param modelType
     * @param converter
     * @param locale
     * @return
     * @throws Converter.ConversionException
     */
    public static <MODELTYPE, PRESENTATIONTYPE> MODELTYPE convertToModel(
            PRESENTATIONTYPE presentationValue, Class<MODELTYPE> modelType,
            Converter<PRESENTATIONTYPE, MODELTYPE> converter, Locale locale)
            throws Converter.ConversionException {
        if (converter != null) {
            /*
             * If there is a converter, always use it. It must convert or throw
             * an exception.
             */
            return converter.convertToModel(presentationValue, locale);
        }

        if (presentationValue == null) {
            // Null should always be passed through the converter but if there
            // is no converter we can safely return null
            return null;
        }

        if (modelType == null) {
            // No model type, return original value
            return (MODELTYPE) presentationValue;
        } else if (modelType.isAssignableFrom(presentationValue.getClass())) {
            // presentation type directly compatible with model type
            return modelType.cast(presentationValue);
        } else {
            throw new Converter.ConversionException(
                    "Unable to convert value of type "
                            + presentationValue.getClass().getName()
                            + " to model type "
                            + modelType
                            + ". No converter is set and the types are not compatible.");
        }

    }

    /**
     * Checks if the given converter can handle conversion between the given
     * presentation and model type
     * 
     * @param converter
     *            The converter to check
     * @param presentationType
     *            The presentation type
     * @param modelType
     *            The model type
     * @return true if the converter supports conversion between the given
     *         presentation and model type, false otherwise
     */
    public static boolean canConverterHandle(Converter<?, ?> converter,
            Class<?> presentationType, Class<?> modelType) {
        if (converter == null) {
            return false;
        }

        if (!modelType.isAssignableFrom(converter.getModelType())) {
            return false;
        }
        if (!presentationType.isAssignableFrom(converter.getPresentationType())) {
            return false;
        }

        return true;
    }
}
