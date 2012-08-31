/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

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
     * custom UI classes. <code>null</code> indicates that the default class
     * loader should be used.
     * 
     * @return the class loader to use, or <code>null</code>
     */
    public ClassLoader getClassLoader();

    /**
     * Returns the MIME type of the specified file, or null if the MIME type is
     * not known. The MIME type is determined by the configuration of the
     * container, and may be specified in a deployment descriptor. Common MIME
     * types are "text/html" and "image/gif".
     * 
     * @param resourceName
     *            a String specifying the name of a file
     * @return a String specifying the file's MIME type
     * 
     * @see ServletContext#getMimeType(String)
     * @see PortletContext#getMimeType(String)
     */
    public String getMimeType(String resourceName);

    /**
     * Gets the properties configured for the deployment, e.g. as init
     * parameters to the servlet or portlet.
     * 
     * @return properties for the application.
     */
    public Properties getInitParameters();

    public Iterator<AddonContextListener> getAddonContextListeners();

    public AddonContext getAddonContext();

    public void setAddonContext(AddonContext vaadinContext);

    /**
     * Returns whether Vaadin is in production mode.
     * 
     * @since 7.0.0
     * 
     * @return true if in production mode, false otherwise.
     */
    public boolean isProductionMode();

    /**
     * Returns whether cross-site request forgery protection is enabled.
     * 
     * @since 7.0.0
     * 
     * @return true if XSRF protection is enabled, false otherwise.
     */
    public boolean isXsrfProtectionEnabled();

    /**
     * Returns the time resources can be cached in the browsers, in seconds.
     * 
     * @since 7.0.0
     * 
     * @return The resource cache time.
     */
    public int getResourceCacheTime();

    /**
     * Returns the number of seconds between heartbeat requests of a UI, or a
     * non-positive number if heartbeat is disabled.
     * 
     * @since 7.0.0
     * 
     * @return The time between heartbeats.
     */
    public int getHeartbeatInterval();

    /**
     * Returns whether UIs that have no other activity than heartbeat requests
     * should be closed after they have been idle the maximum inactivity time
     * enforced by the session.
     * 
     * @see ApplicationContext#getMaxInactiveInterval()
     * 
     * @since 7.0.0
     * 
     * @return True if UIs receiving only heartbeat requests are eventually
     *         closed; false if heartbeat requests extend UI lifetime
     *         indefinitely.
     */
    public boolean isIdleUICleanupEnabled();
}
