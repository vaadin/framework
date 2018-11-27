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
package com.vaadin.osgi.resources.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.vaadin.osgi.resources.OsgiVaadinContributor;
import com.vaadin.osgi.resources.OsgiVaadinResource;
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
    private final Map<Long, Delegate> resourceToRegistration = Collections
            .synchronizedMap(new LinkedHashMap<>());
    private final Map<Long, List<ServiceRegistration<? extends OsgiVaadinResource>>> contributorToRegistrations = Collections
            .synchronizedMap(new LinkedHashMap<>());
    private HttpService httpService;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinTheme.class, policy = ReferencePolicy.DYNAMIC)
    void bindTheme(ServiceReference<OsgiVaadinTheme> themeRef)
            throws ResourceBundleInactiveException, NamespaceException {

        Bundle bundle = themeRef.getBundle();
        BundleContext context = bundle.getBundleContext();

        OsgiVaadinTheme theme = context.getService(themeRef);
        if (theme == null) {
            return;
        }

        VaadinResourceService resourceService = OsgiVaadinResources
                .getService();
        Long serviceId = (Long) themeRef.getProperty(Constants.SERVICE_ID);
        try {
            registerTheme(resourceService, bundle, serviceId, theme);
        } finally {
            context.ungetService(themeRef);
        }
    }

    void unbindTheme(ServiceReference<OsgiVaadinTheme> themeRef) {
        Long serviceId = (Long) themeRef.getProperty(Constants.SERVICE_ID);
        unregisterResource(serviceId);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinWidgetset.class, policy = ReferencePolicy.DYNAMIC)
    void bindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef)
            throws ResourceBundleInactiveException, NamespaceException {
        Bundle bundle = widgetsetRef.getBundle();
        BundleContext context = bundle.getBundleContext();

        OsgiVaadinWidgetset widgetset = context.getService(widgetsetRef);
        if (widgetset == null) {
            return;
        }

        VaadinResourceService service = OsgiVaadinResources.getService();
        Long serviceId = (Long) widgetsetRef.getProperty(Constants.SERVICE_ID);
        try {
            registerWidget(service, bundle, serviceId, widgetset);
        } finally {
            context.ungetService(widgetsetRef);
        }

    }

    void unbindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef) {
        Long serviceId = (Long) widgetsetRef.getProperty(Constants.SERVICE_ID);
        unregisterResource(serviceId);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinResource.class, policy = ReferencePolicy.DYNAMIC)
    void bindResource(ServiceReference<OsgiVaadinResource> resourceRef)
            throws ResourceBundleInactiveException, NamespaceException {
        Bundle bundle = resourceRef.getBundle();
        BundleContext context = bundle.getBundleContext();

        OsgiVaadinResource resource = context.getService(resourceRef);
        if (resource == null) {
            return;
        }

        VaadinResourceService service = OsgiVaadinResources.getService();
        Long serviceId = (Long) resourceRef.getProperty(Constants.SERVICE_ID);
        try {
            registerResource(service, bundle, serviceId, resource);
        } finally {
            context.ungetService(resourceRef);
        }
    }

    void unbindResource(ServiceReference<OsgiVaadinResource> resourceRef) {
        Long serviceId = (Long) resourceRef.getProperty(Constants.SERVICE_ID);
        unregisterResource(serviceId);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinContributor.class, policy = ReferencePolicy.DYNAMIC)
    void bindContributor(ServiceReference<OsgiVaadinContributor> contributorRef)
            throws ResourceBundleInactiveException {
        Bundle bundle = contributorRef.getBundle();
        BundleContext context = bundle.getBundleContext();

        OsgiVaadinContributor contributor = context.getService(contributorRef);
        if (contributor == null) {
            return;
        }
        Long serviceId = (Long) contributorRef
                .getProperty(Constants.SERVICE_ID);
        List<OsgiVaadinResource> contributions = contributor.getContributions();
        List<ServiceRegistration<? extends OsgiVaadinResource>> registrations = new ArrayList<>(
                contributions.size());
        for (final OsgiVaadinResource r : contributions) {
            ServiceRegistration<? extends OsgiVaadinResource> reg;
            if (r instanceof OsgiVaadinTheme) {
                reg = context.registerService(OsgiVaadinTheme.class,
                        (OsgiVaadinTheme) r, null);
            } else if (r instanceof OsgiVaadinWidgetset) {
                reg = context.registerService(OsgiVaadinWidgetset.class,
                        (OsgiVaadinWidgetset) r, null);
            } else {
                reg = context.registerService(OsgiVaadinResource.class, r,
                        null);
            }
            registrations.add(reg);
        }
        contributorToRegistrations.put(serviceId, registrations);
    }

    void unbindContributor(
            ServiceReference<OsgiVaadinContributor> contributorRef) {
        Long serviceId = (Long) contributorRef
                .getProperty(Constants.SERVICE_ID);
        List<ServiceRegistration<? extends OsgiVaadinResource>> registrations = contributorToRegistrations
                .remove(serviceId);
        if (registrations != null) {
            for (ServiceRegistration<? extends OsgiVaadinResource> reg : registrations) {
                reg.unregister();
            }
        }
    }

    @Reference
    void setHttpService(HttpService service) {
        this.httpService = service;
    }

    void unsetHttpService(HttpService service) {
        this.httpService = null;
    }

    /**
     *
     * @throws NamespaceException
     * @since 8.6.0
     */
    @Activate
    protected void activate() throws NamespaceException {
        for (Delegate registration : resourceToRegistration.values()) {
            registerResource(registration);
        }
    }

    /**
     * @since 8.6.0
     */
    @Deactivate
    protected void deactivate() {
        for (final Delegate registration : resourceToRegistration.values()) {
            unregisterResource(registration);
        }
        for (List<ServiceRegistration<? extends OsgiVaadinResource>> registrations : contributorToRegistrations
                .values()) {
            for (ServiceRegistration<? extends OsgiVaadinResource> reg : registrations) {
                reg.unregister();
            }
        }
        resourceToRegistration.clear();
        contributorToRegistrations.clear();
        httpService = null;
    }

    private void registerTheme(VaadinResourceService resourceService,
            Bundle bundle, Long serviceId, OsgiVaadinTheme theme)
            throws NamespaceException {
        String pathPrefix = resourceService.getResourcePathPrefix();

        String alias = PathFormatHelper.getThemeAlias(theme.getName(),
                pathPrefix);
        String path = PathFormatHelper.getThemePath(theme.getName());

        registerResource(alias, path, bundle, serviceId);
    }

    private void registerWidget(VaadinResourceService resourceService,
            Bundle bundle, Long serviceId, OsgiVaadinWidgetset widgetset)
            throws NamespaceException {
        String pathPrefix = resourceService.getResourcePathPrefix();

        String alias = PathFormatHelper.getWidgetsetAlias(widgetset.getName(),
                pathPrefix);
        String path = PathFormatHelper.getWidgetsetPath(widgetset.getName());

        registerResource(alias, path, bundle, serviceId);
    }

    private void registerResource(VaadinResourceService resourceService,
            Bundle bundle, Long serviceId, OsgiVaadinResource resource)
            throws NamespaceException {
        String pathPrefix = resourceService.getResourcePathPrefix();

        String alias = PathFormatHelper.getRootResourceAlias(resource.getName(),
                pathPrefix);
        String path = PathFormatHelper.getRootResourcePath(resource.getName());

        registerResource(alias, path, bundle, serviceId);
    }

    private void registerResource(String alias, String path, Bundle bundle,
            Long serviceId) throws NamespaceException {
        Delegate registration = new Delegate(alias, path, bundle);
        resourceToRegistration.put(serviceId, registration);
        registerResource(registration);
    }

    private void registerResource(Delegate registration)
            throws NamespaceException {
        if (this.httpService != null && !registration.isInitialized()) {
            registration.init(httpService);
            httpService.registerResources(registration.alias, registration.path,
                    registration);
        }
    }

    private void unregisterResource(Long serviceId) {
        Delegate registration = resourceToRegistration.remove(serviceId);
        unregisterResource(registration);
    }

    private void unregisterResource(Delegate registration) {
        if (registration != null && httpService != null) {
            httpService.unregister(registration.alias);
        }
    }

    static final class Delegate implements HttpContext {
        private final String alias;
        private final String path;
        private final Bundle bundle;

        private volatile HttpContext context;

        public Delegate(String alias, String path, Bundle bundle) {
            this.alias = alias;
            this.path = path;
            this.bundle = bundle;
        }

        public void init(HttpService service) {
            context = service.createDefaultHttpContext();
        }

        public boolean isInitialized() {
            return context != null;
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
