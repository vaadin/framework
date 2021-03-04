/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.osgi.resources.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import com.vaadin.osgi.resources.VaadinResourceService;

/**
 * Implementation of {@link VaadinResourceService}. Uses bundle version as a
 * prefix for the /VAADIN/ folder.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
@Component
public class VaadinResourceServiceImpl implements VaadinResourceService {
    private static final String NAMESPACE_PREFIX = "vaadin-%s";
    // it's best practice to select a own context "namespace"
    private static final String CONTEXT_NAME = "com.vaadin";

    private String pathPrefix;
    private ServiceRegistration<ServletContextHelper> servletContextReg;

    @Activate
    public void start(BundleContext context) throws Exception {
        Version version = context.getBundle().getVersion();
        this.setBundleVersion(version.toString());

        // register the vaadin servlet context helper
        Dictionary<String, String> contextProps = new Hashtable<>();
        contextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
                this.getContextName());
        contextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
                "/" + this.getResourcePathPrefix());
        servletContextReg = context.registerService(ServletContextHelper.class,
                new VaadinServletContextFactory(), contextProps);
    }

    @Deactivate
    public void stop() {
        if (servletContextReg != null) {
            servletContextReg.unregister();
            servletContextReg = null;
        }
    }

    @Override
    public void publishTheme(String themeName, HttpService httpService)
            throws NamespaceException {
        String themeAlias = PathFormatHelper.getThemeAlias(themeName,
                pathPrefix);
        String themePath = PathFormatHelper.getThemePath(themeName);
        httpService.registerResources(themeAlias, themePath, null);
    }

    @Override
    public void publishResource(String resource, HttpService httpService)
            throws NamespaceException {
        String alias = PathFormatHelper.getRootResourceAlias(resource,
                pathPrefix);
        String path = PathFormatHelper.getRootResourcePath(resource);
        httpService.registerResources(alias, path, null);
    }

    @Override
    public void publishWidgetset(String widgetset, HttpService httpService)
            throws NamespaceException {
        String widgetsetAlias = PathFormatHelper.getWidgetsetAlias(widgetset,
                pathPrefix);
        String widgetsetPath = PathFormatHelper.getWidgetsetPath(widgetset);
        httpService.registerResources(widgetsetAlias, widgetsetPath, null);
    }

    @Override
    public String getResourcePathPrefix() {
        return this.pathPrefix;
    }

    @Override
    public String getContextName() {
        return CONTEXT_NAME;
    }

    /**
     * Sets the version of the bundle managing this service.
     *
     * <p>
     * This needs to be called before any other method after the service is
     * created.
     *
     * @param bundleVersion
     *            the version of the bundle managing this service
     */
    private void setBundleVersion(String bundleVersion) {
        this.pathPrefix = String.format(NAMESPACE_PREFIX, bundleVersion);
    }
}
