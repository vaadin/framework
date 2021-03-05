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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.osgi.service.http.HttpService;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

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
    private final Map<Long, Delegate<?>> resourceToRegistration = Collections
            .synchronizedMap(new LinkedHashMap<>());
    private final Map<Long, List<ServiceRegistration<? extends OsgiVaadinResource>>> contributorToRegistrations = Collections
            .synchronizedMap(new LinkedHashMap<>());

    private BundleContext vaadinSharedContext;
    private VaadinResourceService vaadinService;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinTheme.class, policy = ReferencePolicy.DYNAMIC)
    void bindTheme(ServiceReference<OsgiVaadinTheme> themeRef) {
        registerResource(themeRef);
    }

    void unbindTheme(ServiceReference<OsgiVaadinTheme> themeRef) {
        unregisterResource(themeRef);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinWidgetset.class, policy = ReferencePolicy.DYNAMIC)
    void bindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef) {
        registerResource(widgetsetRef);
    }

    void unbindWidgetset(ServiceReference<OsgiVaadinWidgetset> widgetsetRef) {
        unregisterResource(widgetsetRef);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinResource.class, policy = ReferencePolicy.DYNAMIC)
    void bindResource(ServiceReference<OsgiVaadinResource> resourceRef) {
        registerResource(resourceRef);
    }

    void unbindResource(ServiceReference<OsgiVaadinResource> resourceRef) {
        unregisterResource(resourceRef);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = OsgiVaadinContributor.class, policy = ReferencePolicy.DYNAMIC)
    void bindContributor(
            ServiceReference<OsgiVaadinContributor> contributorRef) {
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
        if (this.vaadinService == vaadinService) {
            this.vaadinService = null;
        }
    }

    /**
     *
     * @since 8.6.0
     */
    @Activate
    protected void activate(BundleContext context) {
        vaadinSharedContext = context;
        for (Delegate<?> registration : resourceToRegistration.values()) {
            registration.register(vaadinSharedContext, vaadinService);
        }
    }

    /**
     * @since 8.6.0
     */
    @Deactivate
    protected void deactivate() {
        for (final Delegate<?> registration : resourceToRegistration.values()) {
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
        vaadinSharedContext = null;
        vaadinService = null;
    }

    private <T extends OsgiVaadinResource> void registerResource(
            ServiceReference<T> resourceRef) {
        String pattern = (String) resourceRef.getProperty(
                HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN);
        // if this resource contains a http whiteboard property we are done here
        // because we are registering the same service with whiteboard
        // properties we have to filter them here
        if (pattern != null)
            return;
        BundleContext context = resourceRef.getBundle().getBundleContext();
        Long serviceId = (Long) resourceRef.getProperty(Constants.SERVICE_ID);
        Delegate<T> registration = new Delegate<>(resourceRef, context);
        resourceToRegistration.put(serviceId, registration);
        registration.register(vaadinSharedContext, vaadinService);
    }

    private void unregisterResource(
            ServiceReference<? extends OsgiVaadinResource> resourceRef) {
        Long serviceId = (Long) resourceRef.getProperty(Constants.SERVICE_ID);
        unregisterResource(serviceId);
    }

    private void unregisterResource(Long serviceId) {
        if (serviceId == null)
            return;
        Delegate<?> registration = resourceToRegistration.remove(serviceId);
        unregisterResource(registration);
    }

    private void unregisterResource(Delegate<?> registration) {
        if (registration != null) {
            registration.unregister();
        }
    }

    static final class Delegate<T extends OsgiVaadinResource> {
        private final ServiceReference<T> resourceRef;
        // the bundle context who contributed the resource - we reuse that so we
        // can register the http whiteboard resource in the name of the
        // contributing bundle
        private final BundleContext bundleContext;

        private volatile BundleContext vaadinSharedContext;
        private volatile VaadinResourceService vaadinService;
        private volatile ServiceRegistration<? super T> resourceRegistration;

        public Delegate(ServiceReference<T> resourceRef,
                BundleContext bundleContext) {
            this.resourceRef = Objects.requireNonNull(resourceRef);
            this.bundleContext = Objects.requireNonNull(bundleContext);
        }

        public void register(BundleContext vaadinSharedContext,
                VaadinResourceService vaadinService) {
            if (vaadinService != null) {
                this.vaadinService = vaadinService;
            }
            if (vaadinSharedContext != null) {
                this.vaadinSharedContext = vaadinSharedContext;
            }
            // if all dependencies are satisfied we can finally register the
            // http resource
            if (this.vaadinService != null
                    && this.vaadinSharedContext != null) {
                this.registerImpl();
            }
        }

        public void unregister() {
            if (resourceRegistration != null) {
                resourceRegistration.unregister();
            }
            if (vaadinSharedContext != null) {
                // unget the service reference
                vaadinSharedContext.ungetService(resourceRef);
            }
            vaadinService = null;
            vaadinSharedContext = null;
            resourceRegistration = null;
        }

        @SuppressWarnings("unchecked")
        private void registerImpl() {
            // we have already registered if resourceRegistration is set
            if (resourceRegistration != null)
                return;

            T resource = vaadinSharedContext.getService(this.resourceRef);
            // we don't need a path prefix because we register at the vaadin
            // context which handles the prefixing
            String pathPrefix = "";
            Class<? super T> interfaceType;
            String alias;
            String path;
            if (resource instanceof OsgiVaadinWidgetset) {
                alias = PathFormatHelper.getWidgetsetAlias(resource.getName(),
                        pathPrefix);
                // OsgiVaadinWidgetset provides folders so we have to add a
                // wildcard
                alias = alias + "/*";
                path = PathFormatHelper.getWidgetsetPath(resource.getName());
                // save cast because OsgiVaadinWidgetset is a super class of T
                interfaceType = (Class<? super T>) OsgiVaadinWidgetset.class;
            } else if (resource instanceof OsgiVaadinTheme) {
                alias = PathFormatHelper.getThemeAlias(resource.getName(),
                        pathPrefix);
                // OsgiVaadinTheme provides folders so we have to add a wildcard
                alias = alias + "/*";
                path = PathFormatHelper.getThemePath(resource.getName());
                // save cast because OsgiVaadinTheme is a super class of T
                interfaceType = (Class<? super T>) OsgiVaadinTheme.class;
            } else {
                alias = PathFormatHelper
                        .getRootResourceAlias(resource.getName(), pathPrefix);
                path = PathFormatHelper.getRootResourcePath(resource.getName());
                interfaceType = OsgiVaadinResource.class;
            }
            // remove the empty prefixed slash
            alias = alias.substring(1);

            final String contextFilter = "("
                    + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "="
                    + vaadinService.getContextName() + ")";
            // register a OSGi http resource based on the whiteboard pattern
            final Dictionary<String, String> properties = new Hashtable<>();
            properties.put(
                    HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN,
                    alias);
            properties.put(
                    HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX,
                    path);
            properties.put(
                    HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
                    contextFilter);
            resourceRegistration = bundleContext.registerService(interfaceType,
                    resource, properties);
        }
    }
}
