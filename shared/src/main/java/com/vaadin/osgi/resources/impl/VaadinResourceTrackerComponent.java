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
import java.util.Objects;

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
    
    private VaadinResourceService vaadinService;
    private HttpService httpService;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinTheme.class, policy = ReferencePolicy.DYNAMIC)
    void bindTheme(ServiceReference<OsgiVaadinTheme> themeRef) throws NamespaceException {
        registerResource(themeRef);
    }

    void unbindTheme(ServiceReference<OsgiVaadinTheme> themeRef) {
        unregisterResource(themeRef);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinWidgetset.class, policy = ReferencePolicy.DYNAMIC)
    void bindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef) throws NamespaceException {
        registerResource(widgetsetRef);
    }

    void unbindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef) {
        unregisterResource(widgetsetRef);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinResource.class, policy = ReferencePolicy.DYNAMIC)
    void bindResource(ServiceReference<OsgiVaadinResource> resourceRef) throws NamespaceException {
        registerResource(resourceRef);
    }

    void unbindResource(ServiceReference<OsgiVaadinResource> resourceRef) {
        unregisterResource(resourceRef);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinContributor.class, policy = ReferencePolicy.DYNAMIC)
    void bindContributor(ServiceReference<OsgiVaadinContributor> contributorRef) {
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
    void bindVaadinResourceService(VaadinResourceService vaadinService) {
        this.vaadinService = vaadinService;
    }
    
    void unbindVaadinResourceService(VaadinResourceService vaadinService) {
        if(this.vaadinService == vaadinService) {
            this.vaadinService = null;
        }
    }

    @Reference
    void bindHttpService(HttpService service) {
        this.httpService = service;
    }

    void unbindtHttpService(HttpService service) {
        if(this.httpService == service) {
            this.httpService = null;
        }
    }

    /**
     *
     * @throws NamespaceException
     * @since 8.6.0
     */
    @Activate
    protected void activate(BundleContext context) throws NamespaceException {
        for (Delegate registration : resourceToRegistration.values()) {
            registration.register(context, httpService, vaadinService);
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

    private void registerResource(ServiceReference<? extends OsgiVaadinResource> resourceRef) throws NamespaceException {
        BundleContext context = resourceRef.getBundle().getBundleContext();
        OsgiVaadinResource resource = context.getService(resourceRef);
        //service got is unregistered if we get null here
        if(resource == null)
            return;
        Long serviceId = (Long) resourceRef.getProperty(Constants.SERVICE_ID);
        Delegate registration = new Delegate(resource, resourceRef);
        resourceToRegistration.put(serviceId, registration);
        registration.register(context, httpService, vaadinService);
    }

    private void unregisterResource(ServiceReference<? extends OsgiVaadinResource> resourceRef) {
        Long serviceId = (Long) resourceRef.getProperty(Constants.SERVICE_ID);
        unregisterResource(serviceId);
    }

    private void unregisterResource(Long serviceId) {
        if(serviceId == null)
            return;
        Delegate registration = resourceToRegistration.remove(serviceId);
        unregisterResource(registration);
    }

    private void unregisterResource(Delegate registration) {
        if (registration != null) {
            registration.unregister();
        }
    }

    static final class Delegate implements HttpContext {
        private final OsgiVaadinResource resource;
        private final ServiceReference<? extends OsgiVaadinResource> resourceRef;

        private volatile String alias;
        private volatile String path;
        private volatile BundleContext bundleContext;
        private volatile HttpService httpService;
        private volatile HttpContext httpContext;
        private volatile VaadinResourceService vaadinService;

        public Delegate(OsgiVaadinResource resource, ServiceReference<? extends OsgiVaadinResource> resourceRef) {
            this.resource = Objects.requireNonNull(resource);
            this.resourceRef = Objects.requireNonNull(resourceRef);
        }

        public void register(BundleContext bundleContext, HttpService httpService, VaadinResourceService vaadinService) throws NamespaceException {
            if(bundleContext != null) {
                this.bundleContext = bundleContext;
            }
            if(httpService != null) {
                this.httpService = httpService;
                this.httpContext = this.httpService.createDefaultHttpContext();
            }
            if(vaadinService != null) {
                this.vaadinService = vaadinService;
            }
            //if all dependencies are satisfied we can finally register on the HttpService
            if(this.bundleContext != null && this.httpService != null && this.vaadinService != null) {
                this.registerImpl();
            }
        }

        public void unregister() {
            if (httpService != null) {
                httpService.unregister(alias);
            }
            if(bundleContext != null) {
                //unget the service reference
                bundleContext.ungetService(resourceRef);
            }
            alias = null;
            path = null;
            bundleContext = null;
            httpService = null;
            httpContext = null;
            vaadinService = null;
        }

        @Override
        public boolean handleSecurity(HttpServletRequest request,
                HttpServletResponse response) throws IOException {
            return httpContext.handleSecurity(request, response);
        }

        @Override
        public URL getResource(String name) {
            return resourceRef.getBundle().getResource(name);
        }

        @Override
        public String getMimeType(String name) {
            return httpContext.getMimeType(name);
        }

        private void registerImpl() throws NamespaceException {
            //we have already registered if alias is set
            if(alias != null)
                return;
            
            String pathPrefix = vaadinService.getResourcePathPrefix();
            if(resource instanceof OsgiVaadinWidgetset) {
                alias = PathFormatHelper.getRootResourceAlias(resource.getName(),
                        pathPrefix);
                path = PathFormatHelper.getRootResourcePath(resource.getName());
            } else if(resource instanceof OsgiVaadinTheme){
                alias = PathFormatHelper.getThemeAlias(resource.getName(),
                        pathPrefix);
                path = PathFormatHelper.getThemePath(resource.getName());
            } else {
                alias = PathFormatHelper.getRootResourceAlias(resource.getName(), pathPrefix);
                path = PathFormatHelper.getRootResourcePath(resource.getName());
            }
            httpService.registerResources(alias, path,
                    this);
        }
    }
}
