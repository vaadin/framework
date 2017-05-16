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
package com.vaadin.osgi.liferay;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.portlet.Portlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.vaadin.osgi.resources.VaadinResourceService;
import com.vaadin.ui.UI;

/**
 * Tracks {@link UI UIs} registered as OSGi services.
 *
 * <p>
 * If the {@link UI} is annotated with
 * {@link VaadinLiferayPortletConfiguration}, a {@link Portlet} is created for
 * it.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Sampsa Sohlman
 *
 * @since 8.1
 */
class PortletUIServiceTrackerCustomizer
        implements ServiceTrackerCustomizer<UI, ServiceObjects<UI>> {

    private static final String RESOURCE_PATH_PREFIX = "/o/%s";
    private static final String DISPLAY_CATEGORY = PortletProperties.DISPLAY_CATEGORY;
    private static final String VAADIN_CATEGORY = "category.vaadin";

    private static final String PORTLET_NAME = PortletProperties.PORTLET_NAME;
    private static final String DISPLAY_NAME = PortletProperties.DISPLAY_NAME;
    private static final String PORTLET_SECURITY_ROLE = PortletProperties.PORTLET_SECURITY_ROLE;
    private static final String VAADIN_RESOURCE_PATH = "javax.portlet.init-param.vaadin.resources.path";

    private Map<ServiceReference<UI>, ServiceRegistration<Portlet>> portletRegistrations = new HashMap<ServiceReference<UI>, ServiceRegistration<Portlet>>();
    private VaadinResourceService service;

    PortletUIServiceTrackerCustomizer(VaadinResourceService service) {
        this.service = service;
    }

    @Override
    public ServiceObjects<UI> addingService(
            ServiceReference<UI> uiServiceReference) {

        Bundle bundle = uiServiceReference.getBundle();
        BundleContext bundleContext = bundle.getBundleContext();
        UI contributedUI = bundleContext.getService(uiServiceReference);

        try {
            Class<? extends UI> uiClass = contributedUI.getClass();
            VaadinLiferayPortletConfiguration portletConfiguration = uiClass
                    .getAnnotation(VaadinLiferayPortletConfiguration.class);

            boolean isPortletUi = uiServiceReference
                    .getProperty(PortletProperties.PORTLET_UI_PROPERTY) != null
                    || portletConfiguration != null;
            if (isPortletUi) {
                return registerPortlet(uiServiceReference,
                        portletConfiguration);
            } else {
                // No portlet configuration, ignore the UI
                return null;
            }
        } finally {
            bundleContext.ungetService(uiServiceReference);
        }
    }

    private ServiceObjects<UI> registerPortlet(ServiceReference<UI> reference,
            VaadinLiferayPortletConfiguration configuration) {

        Bundle bundle = reference.getBundle();
        BundleContext bundleContext = bundle.getBundleContext();

        ServiceObjects<UI> serviceObjects = bundleContext
                .getServiceObjects(reference);

        OsgiUIProvider uiProvider = new OsgiUIProvider(serviceObjects);

        Dictionary<String, Object> properties = null;
        if (configuration != null) {
            properties = createPortletProperties(uiProvider, reference,
                    configuration);
        } else {
            properties = createPortletProperties(reference);
        }

        OsgiVaadinPortlet portlet = new OsgiVaadinPortlet(uiProvider);

        ServiceRegistration<Portlet> serviceRegistration = bundleContext
                .registerService(Portlet.class, portlet, properties);

        portletRegistrations.put(reference, serviceRegistration);

        return serviceObjects;
    }

    private Dictionary<String, Object> createPortletProperties(
            OsgiUIProvider uiProvider, ServiceReference<UI> reference,
            VaadinLiferayPortletConfiguration configuration) {

        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        String category = configuration.category();
        if (category.trim().isEmpty()) {
            category = VAADIN_CATEGORY;
        }
        copyProperty(reference, properties, DISPLAY_CATEGORY, category);

        String portletName = configuration.name();
        if (portletName.trim().isEmpty()) {
            portletName = uiProvider.getDefaultPortletName();
        }

        String displayName = configuration.displayName();
        if (displayName.trim().isEmpty()) {
            displayName = uiProvider.getDefaultDisplayName();
        }

        copyProperty(reference, properties, PORTLET_NAME, portletName);
        copyProperty(reference, properties, DISPLAY_NAME, displayName);
        copyProperty(reference, properties, PORTLET_SECURITY_ROLE,
                configuration.securityRole());

        String resourcesPath = String.format(RESOURCE_PATH_PREFIX,
                service.getResourcePathPrefix());
        copyProperty(reference, properties, VAADIN_RESOURCE_PATH,
                resourcesPath);

        return properties;
    }

    private void copyProperty(ServiceReference<UI> serviceReference,
            Dictionary<String, Object> properties, String key,
            Object defaultValue) {

        Object value = serviceReference.getProperty(key);
        if (value != null) {
            properties.put(key, value);
        } else if (value == null && defaultValue != null) {
            properties.put(key, defaultValue);
        }
    }

    private Dictionary<String, Object> createPortletProperties(
            ServiceReference<UI> reference) {
        Hashtable<String, Object> properties = new Hashtable<>();
        for (String key : reference.getPropertyKeys()) {
            properties.put(key, reference.getProperty(key));
        }
        String resourcesPath = String.format(RESOURCE_PATH_PREFIX,
                service.getResourcePathPrefix());
        properties.put(VAADIN_RESOURCE_PATH, resourcesPath);

        return properties;
    }

    @Override
    public void modifiedService(ServiceReference<UI> serviceReference,
            ServiceObjects<UI> ui) {
        /*
         * This service has been registered as a portlet at some point,
         * otherwise it wouldn't be tracked.
         *
         * This handles changes for Portlet related properties that are part of
         * the UI service to be passed to the Portlet service registration.
         */
        Dictionary<String, Object> newProperties = createPortletProperties(
                serviceReference);
        ServiceRegistration<Portlet> registration = portletRegistrations
                .get(serviceReference);
        if (registration != null) {
            registration.setProperties(newProperties);
        }
    }

    @Override
    public void removedService(ServiceReference<UI> reference,
            ServiceObjects<UI> ui) {

        ServiceRegistration<Portlet> portletRegistration = portletRegistrations
                .get(reference);
        portletRegistrations.remove(reference);
        portletRegistration.unregister();
    }

    void cleanPortletRegistrations() {
        for (ServiceRegistration<Portlet> registration : portletRegistrations
                .values()) {
            registration.unregister();
        }
        portletRegistrations.clear();
        portletRegistrations = null;
    }
}
