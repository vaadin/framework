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

/**
 * Interface that implements conversion between a model and a presentation type.
 * <p>
 * Typically {@link #convertToPresentation(Object, Class, Locale)} and
 * {@link #convertToModel(Object, Class, Locale)} should be symmetric so that
 * chaining these together returns the original result for all input but this is
 * not a requirement.
 * </p>
 * <p>
 * Converters must not have any side effects (never update UI from inside a
 * converter).
 * </p>
 * <p>
 * All Converters must be stateless and thread safe.
 * </p>
 * <p>
 * If conversion of a value fails, a {@link ConversionException} is thrown.
 * </p>
 * 
 * @param <PRESENTATION>
 *            The presentation type. Must be compatible with what
 *            {@link #getPresentationType()} returns.
 * @param <MODEL>
 *            The model type. Must be compatible with what
 *            {@link #getModelType()} returns.
 * @author Vaadin Ltd.
 * @since 7.0
 */
public interface Converter<PRESENTATION, MODEL> extends Serializable {

    /**
     * Converts the given value from target type to source type.
     * <p>
     * A converter can optionally use locale to do the conversion.
     * </p>
     * A converter should in most cases be symmetric so chaining
     * {@link #convertToPresentation(Object, Class, Locale)} and
     * {@link #convertToModel(Object, Class, Locale)} should return the original
     * value.
     * 
     * @param value
     *            The value to convert, compatible with the target type. Can be
     *            null
     * @param targetType
     *            The requested type of the return value
     * @param locale
     *            The locale to use for conversion. Can be null.
     * @return The converted value compatible with the source type
     * @throws ConversionException
     *             If the value could not be converted
     */
    public MODEL convertToModel(PRESENTATION value,
            Class<? extends MODEL> targetType, Locale locale)
            throws ConversionException;

    /**
     * Converts the given value from source type to target type.
     * <p>
     * A converter can optionally use locale to do the conversion.
     * </p>
     * A converter should in most cases be symmetric so chaining
     * {@link #convertToPresentation(Object, Class, Locale)} and
     * {@link #convertToModel(Object, Class, Locale)} should return the original
     * value.
     * 
     * @param value
     *            The value to convert, compatible with the target type. Can be
     *            null
     * @param targetType
     *            The requested type of the return value
     * @param locale
     *            The locale to use for conversion. Can be null.
     * @return The converted value compatible with the source type
     * @throws ConversionException
     *             If the value could not be converted
     */
    public PRESENTATION convertToPresentation(MODEL value,
            Class<? extends PRESENTATION> targetType, Locale locale)
            throws ConversionException;

    /**
     * The source type of the converter.
     * 
     * Values of this type can be passed to
     * {@link #convertToPresentation(Object, Class, Locale)}.
     * 
     * @return The source type
     */
    public Class<MODEL> getModelType();

    /**
     * The target type of the converter.
     * 
     * Values of this type can be passed to
     * {@link #convertToModel(Object, Class, Locale)}.
     * 
     * @return The target type
     */
    public Class<PRESENTATION> getPresentationType();

    /**
     * An exception that signals that the value passed to
     * {@link Converter#convertToPresentation(Object, Class, Locale)} or
     * {@link Converter#convertToModel(Object, Class, Locale)} could not be
     * converted.
     * 
     * @author Vaadin Ltd
     * @since 7.0
     */
    public static class ConversionException extends RuntimeException {

        /**
         * Constructs a new <code>ConversionException</code> without a detail
         * message.
         */
        public ConversionException() {
        }

        /**
         * Constructs a new <code>ConversionException</code> with the specified
         * detail message.
         * 
         * @param msg
         *            the detail message
         */
        public ConversionException(String msg) {
            super(msg);
        }

        /**
         * Constructs a new {@code ConversionException} with the specified
         * cause.
         * 
         * @param cause
         *            The cause of the the exception
         */
        public ConversionException(Throwable cause) {
            super(cause);
        }

        /**
         * Constructs a new <code>ConversionException</code> with the specified
         * detail message and cause.
         * 
         * @param message
         *            the detail message
         * @param cause
         *            The cause of the the exception
         */
        public ConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
