/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.io.Serializable;
import java.util.Locale;

/**
 * Interface that implements conversion between objects of one type to another
 * and back.
 * <p>
 * Typically {@link #convertFromSourceToTarget(Object, Locale)} and
 * {@link #convertFromTargetToSource(Object, Locale)} should be symmetric so
 * that chaining these together returns the original result for all input but
 * this is not a requirement.
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
 * @param <SOURCE>
 *            The source type. Must be compatible with what
 *            {@link #getSourceType()} returns.
 * @param <TARGET>
 *            The target type. Must be compatible with what
 *            {@link #getTargetType()} returns.
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
public interface Converter<SOURCE, TARGET> extends Serializable {

    /**
     * Converts the given value from target type to source type.
     * <p>
     * A converter can optionally use locale to do the conversion.
     * </p>
     * A converter should in most cases be symmetric so chaining
     * {@link #convertFromSourceToTarget(Object, Locale)} and
     * {@link #convertFromTargetToSource(Object, Locale)} should return the
     * original value.
     * 
     * @param value
     *            The value to convert, compatible with the target type. Can be
     *            null
     * @param locale
     *            The locale to use for conversion. Can be null.
     * @return The converted value compatible with the source type
     * @throws ConversionException
     *             If the value could not be converted
     */
    public SOURCE convertFromTargetToSource(TARGET value, Locale locale)
            throws ConversionException;

    /**
     * Converts the given value from source type to target type.
     * <p>
     * A converter can optionally use locale to do the conversion.
     * </p>
     * A converter should in most cases be symmetric so chaining
     * {@link #convertFromSourceToTarget(Object, Locale)} and
     * {@link #convertFromTargetToSource(Object, Locale)} should return the
     * original value.
     * 
     * @param value
     *            The value to convert, compatible with the target type. Can be
     *            null
     * @param locale
     *            The locale to use for conversion. Can be null.
     * @return The converted value compatible with the source type
     * @throws ConversionException
     *             If the value could not be converted
     */
    public TARGET convertFromSourceToTarget(SOURCE value, Locale locale)
            throws ConversionException;

    /**
     * The source type of the converter.
     * 
     * Values of this type can be passed to
     * {@link #convertFromSourceToTarget(Object, Locale)}.
     * 
     * @return The source type
     */
    public Class<SOURCE> getSourceType();

    /**
     * The target type of the converter.
     * 
     * Values of this type can be passed to
     * {@link #convertFromTargetToSource(Object, Locale)}.
     * 
     * @return The target type
     */
    public Class<TARGET> getTargetType();

    /**
     * An exception that signals that the value passed to
     * {@link Converter#convertFromSourceToTarget(Object, Locale)} or
     * {@link Converter#convertFromTargetToSource(Object, Locale)} could not be
     * converted.
     * 
     * @author Vaadin Ltd
     * @version
     * @VERSION@
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
