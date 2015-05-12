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
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.communication.PushMode;

/**
 * Test for {@link AbstractDeploymentConfiguration}
 * 
 * @author Vaadin Ltd
 */
public class AbstractDeploymentConfigurationTest {

    @Test
    public void getUIClass_returnsUIParameterPropertyValue() {
        String ui = UUID.randomUUID().toString();
        DeploymentConfiguration config = getConfig(VaadinSession.UI_PARAMETER,
                ui);
        Assert.assertEquals("Unexpected UI class configuration option value",
                ui, config.getUIClassName());
    }

    @Test
    public void getUIProviderClass_returnsUIProviderPropertyValue() {
        String uiProvider = UUID.randomUUID().toString();
        DeploymentConfiguration config = getConfig(
                Constants.SERVLET_PARAMETER_UI_PROVIDER, uiProvider);
        Assert.assertEquals(
                "Unexpected UI providerclass configuration option value",
                uiProvider, config.getUIProviderClassName());
    }

    @Test
    public void getWidgetset_returnsWidgetsetProviderPropertyValue() {
        String widgetset = UUID.randomUUID().toString();
        DeploymentConfiguration config = getConfig(
                Constants.PARAMETER_WIDGETSET, widgetset);
        Assert.assertEquals("Unexpected widgetset configuration option value",
                widgetset, config.getWidgetset(null));
    }

    @Test
    public void getWidgetset_noWidgetsetPropertyValue_returnsProvidedDefaultValue() {
        DeploymentConfiguration config = getConfig(null, null);
        String widgetset = UUID.randomUUID().toString();
        Assert.assertEquals("Unexpected widgetset configuration option value",
                widgetset, config.getWidgetset(widgetset));
    }

    @Test
    public void getResourcesPath_returnsResourcesPathPropertyValue() {
        String resources = UUID.randomUUID().toString();
        DeploymentConfiguration config = getConfig(
                Constants.PARAMETER_VAADIN_RESOURCES, resources);
        Assert.assertEquals(
                "Unexpected resources path configuration option value",
                resources, config.getResourcesPath());
    }

    @Test
    public void getClassLoader_returnsClassloaderPropertyValue() {
        String classLoader = UUID.randomUUID().toString();
        DeploymentConfiguration config = getConfig("ClassLoader", classLoader);
        Assert.assertEquals(
                "Unexpected classLoader configuration option value",
                classLoader, config.getClassLoaderName());
    }

    private DeploymentConfiguration getConfig(String property, String value) {
        Properties props = new Properties();
        if (property != null) {
            props.put(property, value);
        }
        return new DeploymentConfigImpl(props);
    }

    private static class DeploymentConfigImpl extends
            AbstractDeploymentConfiguration {

        private Properties properties;

        DeploymentConfigImpl(Properties props) {
            properties = props;
        }

        @Override
        public boolean isProductionMode() {
            return false;
        }

        @Override
        public boolean isXsrfProtectionEnabled() {
            return false;
        }

        @Override
        public boolean isSyncIdCheckEnabled() {
            return false;
        }

        @Override
        public int getResourceCacheTime() {
            return 0;
        }

        @Override
        public int getHeartbeatInterval() {
            return 0;
        }

        @Override
        public boolean isCloseIdleSessions() {
            return false;
        }

        @Override
        public PushMode getPushMode() {
            return null;
        }

        @Override
        public Properties getInitParameters() {
            return null;
        }

        @Override
        public String getApplicationOrSystemProperty(String propertyName,
                String defaultValue) {
            return properties.getProperty(propertyName, defaultValue);
        }

        @Override
        public LegacyProperyToStringMode getLegacyPropertyToStringMode() {
            return null;
        }

        @Override
        public boolean isSendUrlsAsParameters() {
            return DefaultDeploymentConfiguration.DEFAULT_SEND_URLS_AS_PARAMETERS;
        }

    }
}
