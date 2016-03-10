/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.Constants;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration.LegacyProperyToStringMode;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * Annotation for configuring subclasses of {@link VaadinServlet}. For a
 * {@link VaadinServlet} class that has this annotation, the defined values are
 * read during initialization and will be available using
 * {@link DeploymentConfiguration#getApplicationOrSystemProperty(String, String)}
 * as well as from specific methods in {@link DeploymentConfiguration}. Init
 * params defined in <code>web.xml</code> or the <code>@WebServlet</code>
 * annotation take precedence over values defined in this annotation.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VaadinServletConfiguration {
    /**
     * Defines the init parameter name for methods in
     * {@link VaadinServletConfiguration}.
     * 
     * @since 7.1
     * @author Vaadin Ltd
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    public @interface InitParameterName {
        /**
         * The name of the init parameter that the annotated method controls.
         * 
         * @return the parameter name
         */
        public String value();
    }

    /**
     * Whether Vaadin is in production mode.
     * 
     * @return true if in production mode, false otherwise.
     * 
     * @see DeploymentConfiguration#isProductionMode()
     */
    @InitParameterName(Constants.SERVLET_PARAMETER_PRODUCTION_MODE)
    public boolean productionMode();

    /**
     * Gets the default UI class to use for the servlet.
     * 
     * @return the default UI class
     */
    @InitParameterName(VaadinSession.UI_PARAMETER)
    public Class<? extends UI> ui();

    /**
     * The time resources can be cached in the browser, in seconds. The default
     * value is 3600 seconds, i.e. one hour.
     * 
     * @return the resource cache time
     * 
     * @see DeploymentConfiguration#getResourceCacheTime()
     */
    @InitParameterName(Constants.SERVLET_PARAMETER_RESOURCE_CACHE_TIME)
    public int resourceCacheTime() default DefaultDeploymentConfiguration.DEFAULT_RESOURCE_CACHE_TIME;

    /**
     * The number of seconds between heartbeat requests of a UI, or a
     * non-positive number if heartbeat is disabled. The default value is 300
     * seconds, i.e. 5 minutes.
     * 
     * @return the time between heartbeats
     * 
     * @see DeploymentConfiguration#getHeartbeatInterval()
     */
    @InitParameterName(Constants.SERVLET_PARAMETER_HEARTBEAT_INTERVAL)
    public int heartbeatInterval() default DefaultDeploymentConfiguration.DEFAULT_HEARTBEAT_INTERVAL;

    /**
     * Whether a session should be closed when all its open UIs have been idle
     * for longer than its configured maximum inactivity time. The default value
     * is <code>false</code>.
     * 
     * @return true if UIs and sessions receiving only heartbeat requests are
     *         eventually closed; false if heartbeat requests extend UI and
     *         session lifetime indefinitely
     * 
     * @see DeploymentConfiguration#isCloseIdleSessions()
     */
    @InitParameterName(Constants.SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS)
    public boolean closeIdleSessions() default DefaultDeploymentConfiguration.DEFAULT_CLOSE_IDLE_SESSIONS;

    /**
     * The default widgetset to use for the servlet. The default value is
     * <code>com.vaadin.DefaultWidgetSet</code>.
     * 
     * @return the default widgetset name
     */
    @InitParameterName(VaadinServlet.PARAMETER_WIDGETSET)
    public String widgetset() default VaadinServlet.DEFAULT_WIDGETSET;

    /**
     * The legacy Property.toString() mode used. The default value is
     * {@link LegacyProperyToStringMode#DISABLED}
     * 
     * @return The Property.toString() mode in use.
     * 
     * @deprecated as of 7.1, should only be used to ease migration
     */
    @Deprecated
    @InitParameterName(Constants.SERVLET_PARAMETER_LEGACY_PROPERTY_TOSTRING)
    public LegacyProperyToStringMode legacyPropertyToStringMode() default LegacyProperyToStringMode.DISABLED;
}
