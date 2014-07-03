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

package com.vaadin.server;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.shared.communication.PushMode;

/**
 * The default implementation of {@link DeploymentConfiguration} based on a base
 * class for resolving system properties and a set of init parameters.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class DefaultDeploymentConfiguration implements DeploymentConfiguration {
    /**
     * Default value for {@link #getResourceCacheTime()} = {@value} .
     */
    public static final int DEFAULT_RESOURCE_CACHE_TIME = 3600;

    /**
     * Default value for {@link #getHeartbeatInterval()} = {@value} .
     */
    public static final int DEFAULT_HEARTBEAT_INTERVAL = 300;

    /**
     * Default value for {@link #isCloseIdleSessions()} = {@value} .
     */
    public static final boolean DEFAULT_CLOSE_IDLE_SESSIONS = false;

    /**
     * Default value for {@link #getLegacyPropertyToStringMode()} =
     * {@link LegacyProperyToStringMode#WARNING}.
     */
    public static final LegacyProperyToStringMode DEFAULT_LEGACY_PROPERTY_TO_STRING = LegacyProperyToStringMode.WARNING;

    /**
     * Default value for {@link #isSyncIdCheckEnabled()} = {@value} .
     * 
     * @since 7.3
     */
    public static final boolean DEFAULT_SYNC_ID_CHECK = true;

    private final Properties initParameters;
    private boolean productionMode;
    private boolean xsrfProtectionEnabled;
    private int resourceCacheTime;
    private int heartbeatInterval;
    private boolean closeIdleSessions;
    private PushMode pushMode;
    private final Class<?> systemPropertyBaseClass;
    private LegacyProperyToStringMode legacyPropertyToStringMode;
    private boolean syncIdCheck;

    /**
     * Create a new deployment configuration instance.
     * 
     * @param systemPropertyBaseClass
     *            the class that should be used as a basis when reading system
     *            properties
     * @param initParameters
     *            the init parameters that should make up the foundation for
     *            this configuration
     */
    public DefaultDeploymentConfiguration(Class<?> systemPropertyBaseClass,
            Properties initParameters) {
        this.initParameters = initParameters;
        this.systemPropertyBaseClass = systemPropertyBaseClass;

        checkProductionMode();
        checkXsrfProtection();
        checkResourceCacheTime();
        checkHeartbeatInterval();
        checkCloseIdleSessions();
        checkPushMode();
        checkLegacyPropertyToString();
        checkSyncIdCheck();
    }

    private void checkLegacyPropertyToString() {
        String param = getApplicationOrSystemProperty(
                Constants.SERVLET_PARAMETER_LEGACY_PROPERTY_TOSTRING,
                DEFAULT_LEGACY_PROPERTY_TO_STRING.getPropertyString());

        for (LegacyProperyToStringMode mode : LegacyProperyToStringMode
                .values()) {
            if (mode.getPropertyString().equals(param)) {
                legacyPropertyToStringMode = mode;
                return;
            }
        }

        getLogger()
                .log(Level.WARNING,
                        Constants.WARNING_UNKNOWN_LEGACY_PROPERTY_TOSTRING_VALUE,
                        param);

        legacyPropertyToStringMode = DEFAULT_LEGACY_PROPERTY_TO_STRING;
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
            int index = className.lastIndexOf('.');
            if (index >= 0) {
                pkgName = className.substring(0, index);
            } else {
                pkgName = null;
            }
        }
        if (pkgName == null) {
            pkgName = "";
        } else {
            pkgName += '.';
        }
        val = System.getProperty(pkgName + parameterName);
        if (val != null) {
            return val;
        }

        // Try lowercased system properties
        val = System.getProperty(pkgName + parameterName.toLowerCase());
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

    /**
     * {@inheritDoc}
     * <p>
     * The default value is false.
     */
    @Override
    public boolean isCloseIdleSessions() {
        return closeIdleSessions;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default value is <code>true</code>.
     */
    @Override
    public boolean isSyncIdCheckEnabled() {
        return syncIdCheck;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default mode is {@link PushMode#DISABLED}.
     */
    @Override
    public PushMode getPushMode() {
        return pushMode;
    }

    @Override
    public Properties getInitParameters() {
        return initParameters;
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
                            Integer.toString(DEFAULT_RESOURCE_CACHE_TIME)));
        } catch (NumberFormatException e) {
            getLogger().warning(
                    Constants.WARNING_RESOURCE_CACHING_TIME_NOT_NUMERIC);
            resourceCacheTime = DEFAULT_RESOURCE_CACHE_TIME;
        }
    }

    private void checkHeartbeatInterval() {
        try {
            heartbeatInterval = Integer
                    .parseInt(getApplicationOrSystemProperty(
                            Constants.SERVLET_PARAMETER_HEARTBEAT_INTERVAL,
                            Integer.toString(DEFAULT_HEARTBEAT_INTERVAL)));
        } catch (NumberFormatException e) {
            getLogger().warning(
                    Constants.WARNING_HEARTBEAT_INTERVAL_NOT_NUMERIC);
            heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
        }
    }

    private void checkCloseIdleSessions() {
        closeIdleSessions = getApplicationOrSystemProperty(
                Constants.SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS,
                Boolean.toString(DEFAULT_CLOSE_IDLE_SESSIONS)).equals("true");
    }

    private void checkPushMode() {
        String mode = getApplicationOrSystemProperty(
                Constants.SERVLET_PARAMETER_PUSH_MODE,
                PushMode.DISABLED.toString());
        try {
            pushMode = Enum.valueOf(PushMode.class, mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            getLogger().warning(Constants.WARNING_PUSH_MODE_NOT_RECOGNIZED);
            pushMode = PushMode.DISABLED;
        }
    }

    private void checkSyncIdCheck() {
        syncIdCheck = getApplicationOrSystemProperty(
                Constants.SERVLET_PARAMETER_SYNC_ID_CHECK,
                Boolean.toString(DEFAULT_SYNC_ID_CHECK)).equals("true");
    }

    private Logger getLogger() {
        return Logger.getLogger(getClass().getName());
    }

    @Override
    @Deprecated
    public LegacyProperyToStringMode getLegacyPropertyToStringMode() {
        return legacyPropertyToStringMode;
    }

}
