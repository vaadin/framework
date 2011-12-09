/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.io.Serializable;
import java.util.Locale;

public interface Converter<SOURCE, TARGET> extends Serializable {

    public SOURCE convertFromTargetToSource(TARGET value, Locale locale)
            throws ConversionException;

    public TARGET convertFromSourceToTarget(SOURCE value, Locale locale)
            throws ConversionException;

    public Class<SOURCE> getSourceType();

    public Class<TARGET> getTargetType();

    /**
     * An exception that signals that the value passed to #convert or
     * Converter.convertFromTargetToSource could not be converted.
     * 
     * @author Vaadin Ltd
     * @version
     * @VERSION@
     * @since 7.0
     */
    public class ConversionException extends RuntimeException {

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
