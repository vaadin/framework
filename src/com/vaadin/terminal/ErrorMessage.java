/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;

/**
 * Interface for rendering error messages to terminal. All the visible errors
 * shown to user must implement this interface.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface ErrorMessage extends Serializable {

    public enum ErrorLevel {
        /**
         * Error code for informational messages.
         */
        INFORMATION("info", 0),
        /**
         * Error code for warning messages.
         */
        WARNING("warning", 1),
        /**
         * Error code for regular error messages.
         */
        ERROR("error", 2),
        /**
         * Error code for critical error messages.
         */
        CRITICAL("critical", 3),
        /**
         * Error code for system errors and bugs.
         */
        SYSTEMERROR("system", 4);

        String text;
        int errorLevel;

        private ErrorLevel(String text, int errorLevel) {
            this.text = text;
            this.errorLevel = errorLevel;
        }

        /**
         * Textual representation for server-client communication of level
         * 
         * @return String for error severity
         */
        public String getText() {
            return text;
        }

        /**
         * Integer representation of error severity for comparison
         * 
         * @return integer for error severity
         */
        public int intValue() {
            return errorLevel;
        }

        @Override
        public String toString() {
            return text;
        }

    }

    /**
     * @deprecated from 7.0, use {@link ErrorLevel#SYSTEMERROR} instead    
     */
    @Deprecated
    public static final ErrorLevel SYSTEMERROR = ErrorLevel.SYSTEMERROR;

    /**
     * @deprecated from 7.0, use {@link ErrorLevel#CRITICAL} instead    
     */
    @Deprecated
    public static final ErrorLevel CRITICAL = ErrorLevel.CRITICAL;

    /**
     * @deprecated from 7.0, use {@link ErrorLevel#ERROR} instead    
     */

    @Deprecated
    public static final ErrorLevel ERROR = ErrorLevel.ERROR;

    /**
     * @deprecated from 7.0, use {@link ErrorLevel#WARNING} instead    
     */
    @Deprecated
    public static final ErrorLevel WARNING = ErrorLevel.WARNING;

    /**
     * @deprecated from 7.0, use {@link ErrorLevel#INFORMATION} instead    
     */
    @Deprecated
    public static final ErrorLevel INFORMATION = ErrorLevel.INFORMATION;

    /**
     * Gets the errors level.
     * 
     * @return the level of error as an integer.
     */
    public ErrorLevel getErrorLevel();

    /**
     * <p>
     * Paints the error message into a UIDL stream. This method creates the UIDL
     * sequence describing it and outputs it to the given UIDL stream.
     * </p>
     * 
     * @param target
     *            the target UIDL stream where the error should paint itself to.
     * @throws PaintException
     *             if the paint operation failed.
     * @deprecated error messages should not be painted to UIDL but sent
     *             differently
     */
    @Deprecated
    public void paint(PaintTarget target) throws PaintException;

}
