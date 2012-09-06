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

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

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
public interface VaadinService extends Serializable {
    /**
     * Return the URL from where static files, e.g. the widgetset and the theme,
     * are served. In a standard configuration the VAADIN folder inside the
     * returned folder is what is used for widgetsets and themes.
     * 
     * The returned folder is usually the same as the context path and
     * independent of the application.
     * 
     * @param request
     *            the request for which the location should be determined
     * 
     * @return The location of static resources (should contain the VAADIN
     *         directory). Never ends with a slash (/).
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
     * Gets the deployment configuration.
     * 
     * @return the deployment configuration
     */
    public DeploymentConfiguration getDeploymentConfiguration();

    public Iterator<AddonContextListener> getAddonContextListeners();

    public AddonContext getAddonContext();

    public void setAddonContext(AddonContext vaadinContext);

    /**
     * Gets the system messages object
     * 
     * @return the system messages object
     */
    public SystemMessages getSystemMessages();

    /**
     * Returns application context base directory.
     * 
     * Typically an application is deployed in a such way that is has an
     * application directory. For web applications this directory is the root
     * directory of the web applications. In some cases applications might not
     * have an application directory (for example web applications running
     * inside a war).
     * 
     * @return The application base directory or null if the application has no
     *         base directory.
     */
    public File getBaseDirectory();

    /**
     * Gets the Vaadin session associated with this request.
     * 
     * @param request
     *            the request to get a vaadin session for.
     * 
     * @see VaadinSession
     * 
     * @return the vaadin session for the request, or <code>null</code> if no
     *         session is found and this is a request for which a new session
     *         shouldn't be created.
     */
    public VaadinSession getVaadinSession(WrappedRequest request);
}
