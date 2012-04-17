/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;

/**
 * Provide deployment specific settings that are required outside terminal
 * specific code.
 * 
 * @author Vaadin Ltd.
 * 
 * @since 7.0
 */
public interface DeploymentConfiguration extends Serializable {

    /**
     * Gets the base URL of the location of Vaadin's static files.
     * 
     * @param request
     *            the request for which the location should be determined
     * 
     * @return a string with the base URL for static files
     */
    public String getStaticFileLocation(WrappedRequest request);

    /**
     * Gets the widgetset that is configured for this deployment, e.g. from a
     * parameter in web.xml.
     * 
     * @param request
     *            the request for which a widgetset is required
     * @return the name of the widgetset
     */
    public String getConfiguredWidgetset(WrappedRequest request);

    /**
     * Gets the theme that is configured for this deployment, e.g. from a portal
     * parameter or just some sensible default value.
     * 
     * @param request
     *            the request for which a theme is required
     * @return the name of the theme
     */
    public String getConfiguredTheme(WrappedRequest request);

    /**
     * Checks whether the Vaadin application will be rendered on its own in the
     * browser or whether it will be included into some other context. A
     * standalone application may do things that might interfere with other
     * parts of a page, e.g. changing the page title and requesting focus upon
     * loading.
     * 
     * @param request
     *            the request for which the application is loaded
     * @return a boolean indicating whether the application should be standalone
     */
    public boolean isStandalone(WrappedRequest request);

    /**
     * Gets a configured property. The properties are typically read from e.g.
     * web.xml or from system properties of the JVM.
     * 
     * @param propertyName
     *            The simple of the property, in some contexts, lookup might be
     *            performed using variations of the provided name.
     * @param defaultValue
     *            the default value that should be used if no value has been
     *            defined
     * @return the property value, or the passed default value if no property
     *         value is found
     */
    public String getApplicationOrSystemProperty(String propertyName,
            String defaultValue);

    /**
     * Get the class loader to use for loading classes loaded by name, e.g.
     * custom Root classes. <code>null</code> indicates that the default class
     * loader should be used.
     * 
     * @return the class loader to use, or <code>null</code>
     */
    public ClassLoader getClassLoader();
}
