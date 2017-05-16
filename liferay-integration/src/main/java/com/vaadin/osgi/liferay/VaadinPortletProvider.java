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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

import com.vaadin.osgi.resources.OsgiVaadinResources;
import com.vaadin.osgi.resources.VaadinResourceService;
import com.vaadin.ui.UI;

/**
 * Initializes a service tracker with {@link PortletUIServiceTrackerCustomizer}
 * to track {@link UI} service registrations.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Sampsa Sohlman
 *
 * @since 8.1
 */
@Component(immediate = true)
public class VaadinPortletProvider {

    private ServiceTracker<UI, ServiceObjects<UI>> serviceTracker;
    private PortletUIServiceTrackerCustomizer portletUIServiceTrackerCustomizer;

    @Activate
    void activate(ComponentContext componentContext) throws Exception {
        BundleContext bundleContext = componentContext.getBundleContext();
        VaadinResourceService service = OsgiVaadinResources.getService();

        portletUIServiceTrackerCustomizer = new PortletUIServiceTrackerCustomizer(
                service);
        serviceTracker = new ServiceTracker<UI, ServiceObjects<UI>>(
                bundleContext, UI.class, portletUIServiceTrackerCustomizer);
        serviceTracker.open();
    }

    @Deactivate
    void deactivate() {
        if (serviceTracker != null) {
            serviceTracker.close();
            portletUIServiceTrackerCustomizer.cleanPortletRegistrations();
            portletUIServiceTrackerCustomizer = null;
        }

    }
}
