/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;

/**
 * An interface that provides information about the user's terminal.
 * Implementors typically provide additional information using methods not in
 * this interface. </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Terminal extends Serializable {

    /**
     * Gets the name of the default theme for this terminal.
     * 
     * @return the name of the theme that is used by default by this terminal.
     */
    public String getDefaultTheme();

    /**
     * Gets the width of the terminal screen in pixels. This is the width of the
     * screen and not the width available for the application.
     * 
     * @return the width of the terminal screen.
     */
    public int getScreenWidth();

    /**
     * Gets the height of the terminal screen in pixels. This is the height of
     * the screen and not the height available for the application.
     * 
     * @return the height of the terminal screen.
     */
    public int getScreenHeight();

    /**
     * An error event implementation for Terminal.
     */
    public interface ErrorEvent extends Serializable {

        /**
         * Gets the contained throwable, the cause of the error.
         */
        public Throwable getThrowable();

    }

    /**
     * Interface for listening to Terminal errors.
     */
    public interface ErrorListener extends Serializable {

        /**
         * Invoked when a terminal error occurs.
         * 
         * @param event
         *            the fired event.
         */
        public void terminalError(Terminal.ErrorEvent event);
    }
}
