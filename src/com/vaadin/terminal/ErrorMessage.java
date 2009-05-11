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

    /**
     * Error code for system errors and bugs.
     */
    public static final int SYSTEMERROR = 5000;

    /**
     * Error code for critical error messages.
     */
    public static final int CRITICAL = 4000;

    /**
     * Error code for regular error messages.
     */
    public static final int ERROR = 3000;

    /**
     * Error code for warning messages.
     */
    public static final int WARNING = 2000;

    /**
     * Error code for informational messages.
     */
    public static final int INFORMATION = 1000;

    /**
     * Gets the errors level.
     * 
     * @return the level of error as an integer.
     */
    public int getErrorLevel();

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
