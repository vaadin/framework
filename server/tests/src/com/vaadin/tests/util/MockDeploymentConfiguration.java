package com.vaadin.tests.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.shared.communication.PushMode;

public class MockDeploymentConfiguration implements DeploymentConfiguration {

    private boolean productionMode = false;
    private boolean xsrfProtectionEnabled = true;

    private int resourceCacheTime = 12;
    private int heartbeatInterval = 300;
    private boolean closeIdleSessions = false;
    private PushMode pushMode = PushMode.DISABLED;
    private Properties initParameters = new Properties();
    private Map<String, String> applicationOrSystemProperty = new HashMap<String, String>();
    private LegacyProperyToStringMode legacyPropertyToStringMode = LegacyProperyToStringMode.DISABLED;
    private boolean syncIdCheckEnabled = true;

    @Override
    public boolean isProductionMode() {
        return productionMode;
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    @Override
    public boolean isXsrfProtectionEnabled() {
        return xsrfProtectionEnabled;
    }

    @Override
    public boolean isSyncIdCheckEnabled() {
        return syncIdCheckEnabled;
    }

    public void setSyncIdCheckEnabled(boolean syncIdCheckEnabled) {
        this.syncIdCheckEnabled = syncIdCheckEnabled;
    }

    public void setXsrfProtectionEnabled(boolean xsrfProtectionEnabled) {
        this.xsrfProtectionEnabled = xsrfProtectionEnabled;
    }

    @Override
    public int getResourceCacheTime() {
        return resourceCacheTime;
    }

    public void setResourceCacheTime(int resourceCacheTime) {
        this.resourceCacheTime = resourceCacheTime;
    }

    @Override
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public boolean isCloseIdleSessions() {
        return closeIdleSessions;
    }

    public void setCloseIdleSessions(boolean closeIdleSessions) {
        this.closeIdleSessions = closeIdleSessions;
    }

    @Override
    public PushMode getPushMode() {
        return pushMode;
    }

    public void setPushMode(PushMode pushMode) {
        this.pushMode = pushMode;
    }

    @Override
    public Properties getInitParameters() {
        return initParameters;
    }

    public void setInitParameter(String key, String value) {
        initParameters.setProperty(key, value);
    }

    public void setApplicationOrSystemProperty(String key, String value) {
        applicationOrSystemProperty.put(key, value);
    }

    @Override
    public String getApplicationOrSystemProperty(String propertyName,
            String defaultValue) {
        if (applicationOrSystemProperty.containsKey(propertyName)) {
            return applicationOrSystemProperty.get(propertyName);
        } else {
            return defaultValue;
        }
    }

    @Override
    @Deprecated
    public LegacyProperyToStringMode getLegacyPropertyToStringMode() {
        return legacyPropertyToStringMode;
    }

    public void setLegacyPropertyToStringMode(
            LegacyProperyToStringMode legacyPropertyToStringMode) {
        this.legacyPropertyToStringMode = legacyPropertyToStringMode;
    }

}
