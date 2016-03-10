/*
 * Copyright 2000-2014 Vaadin Ltd.
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
     *            the presentation type
     * @param <MODELTYPE>
     *            the model type
     * @param presentationType
     *            the presentation type
     * @param modelType
     *            the model type
     * @param session
     *            the session to use to find a ConverterFactory or null to use
     *            the current session
     * @return a Converter capable of converting between the given types or null
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
     *            the model value to convert
     * @param presentationType
     *            the type of the presentation value
     * @param converter
     *            the converter to use
     * @param locale
     *            the locale to use for conversion
     * @param <PRESENTATIONTYPE>
     *            the presentation type
     * @param <MODELTYPE>
     *            the model type
     * 
     * @return the converted value, compatible with the presentation type, or
     *         the original value if its type is compatible and no converter is
     *         set.
     * @throws Converter.ConversionException
     *             if there was a problem converting the value
     */
    @SuppressWarnings("unchecked")
    public static <PRESENTATIONTYPE, MODELTYPE> PRESENTATIONTYPE convertFromModel(
            MODELTYPE modelValue,
            Class<? extends PRESENTATIONTYPE> presentationType,
            Converter<PRESENTATIONTYPE, MODELTYPE> converter, Locale locale)
            throws Converter.ConversionException {
        if (converter != null) {
            /*
             * If there is a converter, always use it. It must convert or throw
             * an exception.
             */
            PRESENTATIONTYPE presentation = converter.convertToPresentation(
                    modelValue, presentationType, locale);
            if (presentation != null
                    && !presentationType.isInstance(presentation)) {
                throw new Converter.ConversionException(
                        "Converter returned an object of type "
                                + presentation.getClass().getName()
                                + " when expecting "
                                + presentationType.getName());
            }

            return presentation;
        }
        if (modelValue == null) {
            // Null should always be passed through the converter but if there
            // is no converter we can safely return null
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
     * Convert the given value from the presentation (UI) type to model (data
     * source) type.
     * 
     * @param presentationValue
     *            the presentation value to convert
     * @param modelType
     *            the type of the model
     * @param converter
     *            the converter to use
     * @param locale
     *            the locale to use for conversion
     * @param <PRESENTATIONTYPE>
     *            the presentation type
     * @param <MODELTYPE>
     *            the model type
     * 
     * @return the converted value, compatible with the model type, or the
     *         original value if its type is compatible and no converter is set.
     * @throws Converter.ConversionException
     *             if there was a problem converting the value
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
            MODELTYPE model = converter.convertToModel(presentationValue,
                    modelType, locale);
            if (model != null && !modelType.isInstance(model)) {
                throw new Converter.ConversionException(
                        "Converter returned an object of type "
                                + model.getClass().getName()
                                + " when expecting " + modelType.getName());
            }

            return model;
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
     * presentation and model type. Does strict type checking and only returns
     * true if the converter claims it can handle exactly the given types.
     * 
     * @see #canConverterPossiblyHandle(Converter, Class, Class)
     * 
     * @param converter
     *            The converter to check. If this is null the result is always
     *            false.
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

        if (modelType != converter.getModelType()) {
            return false;
        }
        if (presentationType != converter.getPresentationType()) {
            return false;
        }

        return true;
    }

    /**
     * Checks if it possible that the given converter can handle conversion
     * between the given presentation and model type somehow.
     * 
     * @param converter
     *            The converter to check. If this is null the result is always
     *            false.
     * @param presentationType
     *            The presentation type
     * @param modelType
     *            The model type
     * @return true if the converter possibly support conversion between the
     *         given presentation and model type, false otherwise
     */
    public static boolean canConverterPossiblyHandle(Converter<?, ?> converter,
            Class<?> presentationType, Class<?> modelType) {
        if (converter == null) {
            return false;
        }
        Class<?> converterModelType = converter.getModelType();

        if (!modelType.isAssignableFrom(converterModelType)
                && !converterModelType.isAssignableFrom(modelType)) {
            // model types are not compatible in any way
            return false;
        }

        Class<?> converterPresentationType = converter.getPresentationType();
        if (!presentationType.isAssignableFrom(converterPresentationType)
                && !converterPresentationType
                        .isAssignableFrom(presentationType)) {
            // presentation types are not compatible in any way
            return false;
        }

        return true;
    }
}
