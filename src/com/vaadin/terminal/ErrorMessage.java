/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;

/**
 * Interface for rendering error messages to terminal. All the visible errors
 * shown to user must implement this interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface ErrorMessage extends Paintable, Serializable {

    public enum ErrorLevel {
        /**
         * Error code for informational messages.
         */
        INFORMATION,
        /**
         * Error code for warning messages.
         */
        WARNING,
        /**
         * Error code for regular error messages.
         */
        ERROR,
        /**
         * Error code for critical error messages.
         */
        CRITICAL,
        /**
         * Error code for system errors and bugs.
         */
        SYSTEMERROR;
    }

    @Deprecated
    public static final ErrorLevel SYSTEMERROR = ErrorLevel.SYSTEMERROR;
    @Deprecated
    public static final ErrorLevel CRITICAL = ErrorLevel.CRITICAL;
    @Deprecated
    public static final ErrorLevel ERROR = ErrorLevel.ERROR;
    @Deprecated
    public static final ErrorLevel WARNING = ErrorLevel.WARNING;
    @Deprecated
    public static final ErrorLevel INFORMATION = ErrorLevel.INFORMATION;

    /**
     * Gets the errors level.
     * 
     * @return the level of error as an integer.
     */
    public ErrorLevel getErrorLevel();

    /**
     * Error messages are inmodifiable and thus listeners are not needed. This
     * method should be implemented as empty.
     * 
     * @param listener
     *            the listener to be added.
     * @see com.vaadin.terminal.Paintable#addListener(Paintable.RepaintRequestListener)
     */
    public void addListener(RepaintRequestListener listener);

    /**
     * Error messages are inmodifiable and thus listeners are not needed. This
     * method should be implemented as empty.
     * 
     * @param listener
     *            the listener to be removed.
     * @see com.vaadin.terminal.Paintable#removeListener(Paintable.RepaintRequestListener)
     */
    public void removeListener(RepaintRequestListener listener);

    /**
     * Error messages are inmodifiable and thus listeners are not needed. This
     * method should be implemented as empty.
     * 
     * @see com.vaadin.terminal.Paintable#requestRepaint()
     */
    public void requestRepaint();

}
