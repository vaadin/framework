/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;
import java.util.Map;

import com.vaadin.ui.Window;

/**
 * {@code ParameterHandler} is implemented by classes capable of handling
 * external parameters.
 * 
 * <p>
 * What parameters are provided depend on what the {@link Terminal} provides and
 * if the application is deployed as a servlet or portlet. URL GET parameters
 * are typically provided to the {@link #handleParameters(Map)} method.
 * </p>
 * <p>
 * A {@code ParameterHandler} must be registered to a {@code Window} using
 * {@link Window#addParameterHandler(ParameterHandler)} to be called when
 * parameters are available.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface ParameterHandler extends Serializable {

    /**
     * Handles the given parameters. All parameters names are of type
     * {@link String} and the values are {@link String} arrays.
     * 
     * @param parameters
     *            an unmodifiable map which contains the parameter names and
     *            values
     * 
     */
    public void handleParameters(Map<String, String[]> parameters);

    /**
     * An ErrorEvent implementation for ParameterHandler.
     */
    public interface ErrorEvent extends Terminal.ErrorEvent {

        /**
         * Gets the ParameterHandler that caused the error.
         * 
         * @return the ParameterHandler that caused the error
         */
        public ParameterHandler getParameterHandler();

    }

}
