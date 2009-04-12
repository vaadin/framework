/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface implemented by all the classes capable of handling external
 * parameters.
 * 
 * <p>
 * Some terminals can provide external parameters for application. For example
 * GET and POST parameters are passed to application as external parameters on
 * Web Adapter. The parameters can be received at any time during the
 * application lifecycle. All the parameter handlers implementing this interface
 * and registered to {@link com.itmill.toolkit.ui.Window} receive all the
 * parameters got from the terminal in the given window.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface ParameterHandler extends Serializable{

    /**
     * <p>
     * Handles the given parameters. The parameters are given as inmodifieable
     * name to value map. All parameters names are of type:
     * {@link java.lang.String}. All the parameter values are arrays of strings.
     * </p>
     * 
     * @param parameters
     *            the Inmodifiable name to value[] mapping.
     * 
     */
    public void handleParameters(Map parameters);

    /**
     * ParameterHandler error event.
     */
    public interface ErrorEvent extends Terminal.ErrorEvent {

        /**
         * Gets the source ParameterHandler.
         * 
         * @return the source Parameter Handler.
         */
        public ParameterHandler getParameterHandler();

    }

}
