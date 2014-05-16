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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.UI;

public class VaadinPortletServiceTests {

    private VaadinPortletService sut;
    private VaadinPortletRequest request;
    private DeploymentConfiguration conf;

    @Before
    public void setup() throws ServiceException {
        VaadinPortlet portlet = mock(VaadinPortlet.class);
        conf = mock(DeploymentConfiguration.class);

        sut = new VaadinPortletService(portlet, conf);

        request = mock(VaadinPortletRequest.class);
    }

    private void mockFileLocationProperty(String location) {
        mockPortalProperty(Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH,
                location);
    }

    private void mockPortalProperty(String name, String value) {
        when(request.getPortalProperty(name)).thenReturn(value);
    }

    private void mockFileLocationPreference(String location) {
        when(
                request.getPortletPreference(Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH))
                .thenReturn(location);
    }

    private void mockLocationDeploymentConfiguration(String location) {
        when(
                conf.getApplicationOrSystemProperty(
                        Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH, null))
                .thenReturn(location);
    }

    private String getStaticFileLocation() {
        return sut.getStaticFileLocation(request);
    }

    private String getTheme() {
        return sut.getConfiguredTheme(request);
    }

    private void mockThemeProperty(String theme) {
        mockPortalProperty(Constants.PORTAL_PARAMETER_VAADIN_THEME, theme);
    }

    private void mockWidgetsetProperty(String widgetset) {
        mockPortalProperty(Constants.PORTAL_PARAMETER_VAADIN_WIDGETSET,
                widgetset);
    }

    private void mockWidgetsetConfiguration(String widgetset) {
        when(
                conf.getApplicationOrSystemProperty(
                        Constants.PARAMETER_WIDGETSET, null)).thenReturn(
                widgetset);
    }

    @Test
    public void preferencesOverrideDeploymentConfiguration() {
        mockFileLocationPreference("prefs");
        mockLocationDeploymentConfiguration("conf");

        String location = getStaticFileLocation();

        assertThat(location, is("prefs"));
    }

    @Test
    public void deploymentConfigurationOverridesProperties() {
        mockFileLocationPreference(null);
        mockLocationDeploymentConfiguration("conf");
        mockFileLocationProperty("props");

        String location = getStaticFileLocation();

        assertThat(location, is("conf"));
    }

    @Test
    public void defaultFileLocationIsSet() {
        mockFileLocationPreference(null);
        mockLocationDeploymentConfiguration(null);
        mockFileLocationProperty(null);

        String location = getStaticFileLocation();

        assertThat(location, is("/html"));
    }

    @Test
    public void trailingSlashesAreTrimmedFromStaticFileLocation() {
        mockFileLocationPreference("/content////");

        String staticFileLocation = getStaticFileLocation();

        assertThat(staticFileLocation, is("/content"));
    }

    @Test
    public void themeCanBeOverridden() {
        mockThemeProperty("foobar");

        String theme = getTheme();

        assertThat(theme, is("foobar"));
    }

    @Test
    public void defaultThemeIsSet() {
        mockThemeProperty(null);

        String theme = getTheme();

        assertThat(theme, is(Constants.DEFAULT_THEME_NAME));
    }

    private String getWidgetset() {
        return sut.getConfiguredWidgetset(request);
    }

    @Test
    public void defaultWidgetsetIsSet() {
        mockWidgetsetProperty(null);
        mockWidgetsetConfiguration(null);

        String widgetset = getWidgetset();

        assertThat(widgetset, is(Constants.DEFAULT_WIDGETSET));
    }

    @Test
    public void configurationWidgetsetOverridesProperty() {
        mockWidgetsetProperty("foo");
        mockWidgetsetConfiguration("bar");

        String widgetset = getWidgetset();

        assertThat(widgetset, is("bar"));
    }

    @Test
    public void oldDefaultWidgetsetIsMappedToDefaultWidgetset() {
        mockWidgetsetConfiguration(null);
        mockWidgetsetProperty("com.vaadin.portal.gwt.PortalDefaultWidgetSet");

        String widgetset = getWidgetset();

        assertThat(widgetset, is(Constants.DEFAULT_WIDGETSET));
    }

    @Test
    public void oldDefaultWidgetSetIsNotMappedToDefaultWidgetset() {
        mockWidgetsetConfiguration("com.vaadin.portal.gwt.PortalDefaultWidgetSet");
        mockWidgetsetProperty(null);

        String widgetset = getWidgetset();

        assertThat(widgetset,
                is("com.vaadin.portal.gwt.PortalDefaultWidgetSet"));
    }

    @Test
    public void findUIDoesntThrowNPE() {
        try {
            ReentrantLock mockLock = Mockito.mock(ReentrantLock.class);
            when(mockLock.isHeldByCurrentThread()).thenReturn(true);

            WrappedSession emptyWrappedSession = Mockito
                    .mock(WrappedSession.class);
            when(emptyWrappedSession.getAttribute("null.lock")).thenReturn(
                    mockLock);
            VaadinRequest requestWithUIIDSet = Mockito
                    .mock(VaadinRequest.class);
            when(requestWithUIIDSet.getParameter(UIConstants.UI_ID_PARAMETER))
                    .thenReturn("1");
            when(requestWithUIIDSet.getWrappedSession()).thenReturn(
                    emptyWrappedSession);

            UI ui = sut.findUI(requestWithUIIDSet);
            Assert.assertNull("Unset session did not return null", ui);
        } catch (NullPointerException e) {
            Assert.fail("findUI threw a NullPointerException");
        }
    }
}
