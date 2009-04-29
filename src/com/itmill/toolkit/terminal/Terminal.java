/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

import java.io.Serializable;

/**
 * Interface for different terminal types.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Terminal extends Serializable {

    /**
     * Gets the name of the default theme.
     * 
     * @return the Name of the terminal window.
     */
    public String getDefaultTheme();

    /**
     * Gets the width of the terminal window in pixels.
     * 
     * @return the Width of the terminal window.
     */
    public int getScreenWidth();

    /**
     * Gets the height of the terminal window in pixels.
     * 
     * @return the Height of the terminal window.
     */
    public int getScreenHeight();

    /**
     * Terminal error event.
     */
    public interface ErrorEvent extends Serializable{

        /**
         * Gets the contained throwable.
         */
        public Throwable getThrowable();

    }

    /**
     * Terminal error listener interface.
     */
    public interface ErrorListener extends Serializable{

        /**
         * Invoked when terminal error occurs.
         * 
         * @param event
         *            the fired event.
         */
        public void terminalError(Terminal.ErrorEvent event);
    }
}
