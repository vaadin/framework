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

package com.vaadin;

import java.util.Properties;
import java.util.logging.Logger;

import com.vaadin.server.Constants;
import com.vaadin.server.DeploymentConfiguration;

public class DefaultDeploymentConfiguration implements DeploymentConfiguration {
    private final Properties initParameters;
    private boolean productionMode;
    private boolean xsrfProtectionEnabled;
    private int resourceCacheTime;
    private int heartbeatInterval;
    private boolean idleRootCleanupEnabled;
    private final Class<?> systemPropertyBaseClass;

    public DefaultDeploymentConfiguration(Class<?> systemPropertyBaseClass,
            Properties initParameters) {
        this.initParameters = initParameters;
        this.systemPropertyBaseClass = systemPropertyBaseClass;

        checkProductionMode();
        checkXsrfProtection();
        checkResourceCacheTime();
        checkHeartbeatInterval();
        checkIdleUICleanup();
    }

    @Override
    public String getApplicationOrSystemProperty(String propertyName,
            String defaultValue) {
        String val = null;

        // Try application properties
        val = getApplicationProperty(propertyName);
        if (val != null) {
            return val;
        }

        // Try system properties
        val = getSystemProperty(propertyName);
        if (val != null) {
            return val;
        }

        return defaultValue;
    }

    /**
     * Gets an system property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    protected String getSystemProperty(String parameterName) {
        String val = null;

        String pkgName;
        final Package pkg = systemPropertyBaseClass.getPackage();
        if (pkg != null) {
            pkgName = pkg.getName();
        } else {
            final String className = systemPropertyBaseClass.getName();
            pkgName = new String(className.toCharArray(), 0,
                    className.lastIndexOf('.'));
        }
        val = System.getProperty(pkgName + "." + parameterName);
        if (val != null) {
            return val;
        }

        // Try lowercased system properties
        val = System.getProperty(pkgName + "." + parameterName.toLowerCase());
        return val;
    }

    /**
     * Gets an application property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    public String getApplicationProperty(String parameterName) {

        String val = initParameters.getProperty(parameterName);
        if (val != null) {
            return val;
        }

        // Try lower case application properties for backward compatibility with
        // 3.0.2 and earlier
        val = initParameters.getProperty(parameterName.toLowerCase());

        return val;
    }

    /**
     * {@inheritDoc}
     * 
     * The default is false.
     */
    @Override
    public boolean isProductionMode() {
        return productionMode;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default is true.
     */
    @Override
    public boolean isXsrfProtectionEnabled() {
        return xsrfProtectionEnabled;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default interval is 3600 seconds (1 hour).
     */
    @Override
    public int getResourceCacheTime() {
        return resourceCacheTime;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default interval is 300 seconds (5 minutes).
     */
    @Override
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    @Override
    public boolean isIdleUICleanupEnabled() {
        return idleRootCleanupEnabled;
    }

    /**
     * Log a warning if Vaadin is not running in production mode.
     */
    private void checkProductionMode() {
        productionMode = getApplicationOrSystemProperty(
                Constants.SERVLET_PARAMETER_PRODUCTION_MODE, "false").equals(
                "true");
        if (!productionMode) {
            getLogger().warning(Constants.NOT_PRODUCTION_MODE_INFO);
        }
    }

    /**
     * Log a warning if cross-site request forgery protection is disabled.
     */
    private void checkXsrfProtection() {
        xsrfProtectionEnabled = !getApplicationOrSystemProperty(
                Constants.SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION, "false")
                .equals("true");
        if (!xsrfProtectionEnabled) {
            getLogger().warning(Constants.WARNING_XSRF_PROTECTION_DISABLED);
        }
    }

    /**
     * Log a warning if resource cache time is set but is not an integer.
     */
    private void checkResourceCacheTime() {
        try {
            resourceCacheTime = Integer
                    .parseInt(getApplicationOrSystemProperty(
                            Constants.SERVLET_PARAMETER_RESOURCE_CACHE_TIME,
                            "3600"));
        } catch (NumberFormatException e) {
            getLogger().warning(
                    Constants.WARNING_RESOURCE_CACHING_TIME_NOT_NUMERIC);
            resourceCacheTime = 3600;
        }
    }

    private void checkHeartbeatInterval() {
        try {
            heartbeatInterval = Integer
                    .parseInt(getApplicationOrSystemProperty(
                            Constants.SERVLET_PARAMETER_HEARTBEAT_RATE, "300"));
        } catch (NumberFormatException e) {
            getLogger().warning(
                    Constants.WARNING_HEARTBEAT_INTERVAL_NOT_NUMERIC);
            heartbeatInterval = 300;
        }
    }

    private void checkIdleUICleanup() {
        idleRootCleanupEnabled = getApplicationOrSystemProperty(
                Constants.SERVLET_PARAMETER_CLOSE_IDLE_UIS, "false").equals(
                "true");
    }

    private Logger getLogger() {
        return Logger.getLogger(getClass().getName());
    }

    @Override
    public Properties getInitParameters() {
        return initParameters;
    }

}
