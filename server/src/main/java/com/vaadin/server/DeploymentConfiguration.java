/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.util.Properties;

import com.vaadin.shared.communication.PushMode;

/**
 * A collection of properties configured at deploy time as well as a way of
 * accessing third party properties not explicitly supported by this class.
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface DeploymentConfiguration extends Serializable {

    /**
     * Returns whether Vaadin is in production mode.
     *
     * @return true if in production mode, false otherwise.
     */
    public boolean isProductionMode();

    /**
     * Returns whether cross-site request forgery protection is enabled.
     *
     * @return true if XSRF protection is enabled, false otherwise.
     */
    public boolean isXsrfProtectionEnabled();

    /**
     * Returns whether sync id checking is enabled. The sync id is used to
     * gracefully handle situations when the client sends a message to a
     * connector that has recently been removed on the server.
     *
     * @since 7.3
     * @return <code>true</code> if sync id checking is enabled;
     *         <code>false</code> otherwise
     */
    public boolean isSyncIdCheckEnabled();

    /**
     * Returns the time resources can be cached in the browsers, in seconds.
     *
     * @return The resource cache time.
     */
    public int getResourceCacheTime();

    /**
     * Returns the number of seconds between heartbeat requests of a UI, or a
     * non-positive number if heartbeat is disabled.
     *
     * @return The time between heartbeats.
     */
    public int getHeartbeatInterval();

    /**
     * Returns whether the sending of URL's as GET and POST parameters in
     * requests with content-type <code>application/x-www-form-urlencoded</code>
     * is enabled or not.
     *
     * @return <code>false</code> if set to false or <code>true</code> otherwise
     */
    public boolean isSendUrlsAsParameters();

    /**
     * Returns whether a session should be closed when all its open UIs have
     * been idle for longer than its configured maximum inactivity time.
     * <p>
     * A UI is idle if it is open on the client side but has no activity other
     * than heartbeat requests. If {@code isCloseIdleSessions() == false},
     * heartbeat requests cause the session to stay open for as long as there
     * are open UIs on the client side. If it is {@code true}, the session is
     * eventually closed if the open UIs do not have any user interaction.
     *
     * @see WrappedSession#getMaxInactiveInterval()
     *
     * @since 7.0.0
     *
     * @return True if UIs and sessions receiving only heartbeat requests are
     *         eventually closed; false if heartbeat requests extend UI and
     *         session lifetime indefinitely.
     */
    public boolean isCloseIdleSessions();

    /**
     * Returns the mode of bidirectional ("push") client-server communication
     * that should be used.
     *
     * @return The push mode in use.
     */
    public PushMode getPushMode();

    /**
     * Gets the properties configured for the deployment, e.g. as init
     * parameters to the servlet or portlet.
     *
     * @return properties for the application.
     */
    public Properties getInitParameters();

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
     * Gets UI class configuration option value.
     *
     * @return UI class name
     *
     * @since 7.4
     */
    public String getUIClassName();

    /**
     * Gets UI provider class configuration option value.
     *
     * @since 7.4
     *
     * @return UI class name
     */
    public String getUIProviderClassName();

    /**
     * Gets Widgetset configuration option value. {@code defaultValue} is
     * returned if widgetset parameter is not configured.
     *
     * @since 7.4
     *
     * @return UI class name
     */
    public String getWidgetset(String defaultValue);

    /**
     * Gets resources path configuration option value.
     *
     * @since 7.4
     */
    public String getResourcesPath();

    /**
     * Gets class loader configuration option value.
     *
     * @since 7.4
     */
    public String getClassLoaderName();

}
