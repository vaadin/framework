/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.vaadin.osgi.resources.OsgiVaadinResources;
import com.vaadin.osgi.resources.OsgiVaadinResources.ResourceBundleInactiveException;
import com.vaadin.osgi.resources.OsgiVaadinTheme;
import com.vaadin.osgi.resources.OsgiVaadinWidgetset;
import com.vaadin.osgi.resources.VaadinResourceService;

/**
 * Tracks {@link OsgiVaadinWidgetset} and {@link OsgiVaadinTheme} registration
 * and uses {@link HttpService} to register them.
 * 
 * @author Vaadin Ltd.
 * 
 * @since 8.1
 */
@Component(immediate = true)
public class VaadinResourceTrackerComponent {
    private HttpService httpService;

    private Map<Long, String> themeToAlias = Collections
            .synchronizedMap(new LinkedHashMap<>());
    private Map<Long, String> widgetsetToAlias = Collections
            .synchronizedMap(new LinkedHashMap<>());

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinTheme.class, policy = ReferencePolicy.DYNAMIC)
    void bindTheme(ServiceReference<OsgiVaadinTheme> themeRef)
            throws ResourceBundleInactiveException, NamespaceException {

        Bundle bundle = themeRef.getBundle();
        BundleContext context = bundle.getBundleContext();

        OsgiVaadinTheme theme = context.getService(themeRef);
        if (theme == null)
            return;

        VaadinResourceService resourceService = OsgiVaadinResources
                .getService();

        try {
            String pathPrefix = resourceService.getResourcePathPrefix();
            Long serviceId = (Long) themeRef.getProperty(Constants.SERVICE_ID);

            String alias = PathFormatHelper.getThemeAlias(theme.getName(),
                    pathPrefix);
            String path = PathFormatHelper.getThemePath(theme.getName());

            httpService.registerResources(alias, path,
                    new Delegate(httpService, bundle));

            themeToAlias.put(serviceId, alias);
        } finally {
            context.ungetService(themeRef);
        }
    }

    void unbindTheme(ServiceReference<OsgiVaadinTheme> themeRef) {
        Long serviceId = (Long) themeRef.getProperty(Constants.SERVICE_ID);
        String themeAlias = themeToAlias.remove(serviceId);
        if (themeAlias != null && httpService != null) {
            httpService.unregister(themeAlias);
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinWidgetset.class, policy = ReferencePolicy.DYNAMIC)
    void bindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef)
            throws ResourceBundleInactiveException, NamespaceException {
        Bundle bundle = widgetsetRef.getBundle();
        BundleContext context = bundle.getBundleContext();

        OsgiVaadinWidgetset widgetset = context.getService(widgetsetRef);
        if (widgetset == null)
            return;

        VaadinResourceService service = OsgiVaadinResources.getService();
        try {
            String pathPrefix = service.getResourcePathPrefix();

            Long serviceId = (Long) widgetsetRef
                    .getProperty(Constants.SERVICE_ID);

            String alias = PathFormatHelper
                    .getWidgetsetAlias(widgetset.getName(), pathPrefix);
            String path = PathFormatHelper
                    .getWidgetsetPath(widgetset.getName());

            httpService.registerResources(alias, path,
                    new Delegate(httpService, bundle));
            widgetsetToAlias.put(serviceId, alias);
        } finally {
            context.ungetService(widgetsetRef);
        }

    }

    void unbindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef) {
        Long serviceId = (Long) widgetsetRef.getProperty(Constants.SERVICE_ID);
        String widgetsetAlias = widgetsetToAlias.remove(serviceId);
        if (widgetsetAlias != null && httpService != null) {
            httpService.unregister(widgetsetAlias);
        }
    }

    @Reference
    void setHttpService(HttpService service) {
        this.httpService = service;
    }

    void unsetHttpService(HttpService service) {
        this.httpService = null;
    }

    static final class Delegate implements HttpContext {
        private HttpContext context;
        private Bundle bundle;

        public Delegate(HttpService service, Bundle bundle) {
            this.context = service.createDefaultHttpContext();
            this.bundle = bundle;
        }

        @Override
        public boolean handleSecurity(HttpServletRequest request,
                HttpServletResponse response) throws IOException {
            return context.handleSecurity(request, response);
        }

        @Override
        public URL getResource(String name) {
            return bundle.getResource(name);
        }

        @Override
        public String getMimeType(String name) {
            return context.getMimeType(name);
        }

    }
}
