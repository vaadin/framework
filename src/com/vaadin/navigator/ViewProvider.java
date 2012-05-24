/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.navigator;

import java.io.Serializable;

/**
 * A provider for view instances that can return pre-registered views or
 * dynamically create new views.
 * 
 * If multiple providers are used, {@link #getViewName(String)} of each is
 * called (in registration order) until one of them returns a non-null value.
 * The {@link #getView(String)} method of that provider is then used.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface ViewProvider extends Serializable {
    /**
     * Extract the view name from a combined view name and parameter string.
     * This method should return a view name if and only if this provider
     * handles creation of such views.
     * 
     * @param viewAndParameters
     *            string with view name and its fragment parameters (if given),
     *            not null
     * @return view name if the view is handled by this provider, null otherwise
     */
    public String getViewName(String viewAndParameters);

    /**
     * Create or return a pre-created instance of a view.
     * 
     * The parameters for the view are set separately by the navigator when the
     * view is activated.
     * 
     * @param viewName
     *            name of the view, not null
     * @return newly created view (null if none available for the view name)
     */
    public View getView(String viewName);
}