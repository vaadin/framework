/* 
 * Copyright 2011 Vaadin Ltd.
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.vaadin.event.EventRouter;
import com.vaadin.util.ReflectTools;

/**
 * Point of entry for add-ons for integrating into various aspects of the
 * framework. One add-on context is initialized for each Vaadin Servlet or
 * Portlet instance and upon initialization, every {@link AddonContextListener}
 * that can be found is notified to let it add listeners to the context.
 * <p>
 * By default, AddonContextListeners are loaded using {@link ServiceLoader},
 * which means that the file
 * META-INF/services/com.vaadin.server.AddonContextListener will be checked for
 * lines containing fully qualified names of classes to use. This behavior can
 * however be overridden for custom deployment situations (e.g. to use CDI or
 * OSGi) by overriding
 * {@link DeploymentConfiguration#getAddonContextListeners()}.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class AddonContext {
    private static final Method APPLICATION_STARTED_METHOD = ReflectTools
            .findMethod(ApplicationStartedListener.class, "applicationStarted",
                    ApplicationStartedEvent.class);

    private final DeploymentConfiguration deploymentConfiguration;

    private final EventRouter eventRouter = new EventRouter();

    private List<BootstrapListener> bootstrapListeners = new ArrayList<BootstrapListener>();

    private List<AddonContextListener> initedListeners = new ArrayList<AddonContextListener>();

    /**
     * Creates a new context using a given deployment configuration. Only the
     * framework itself should typically create AddonContext methods.
     * 
     * @param deploymentConfiguration
     *            the deployment configuration for the associated servlet or
     *            portlet.
     */
    public AddonContext(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;
        deploymentConfiguration.setAddonContext(this);
    }

    /**
     * Gets the deployment configuration for this context.
     * 
     * @return the deployment configuration
     */
    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    /**
     * Initializes this context, causing all found listeners to be notified.
     * Listeners are by default found using {@link ServiceLoader}, but the
     * {@link DeploymentConfiguration} can provide an alternative
     * implementation.
     * <p>
     * This method is not intended to be used by add-ons, but instead by the
     * part of the framework that created this context object.
     */
    public void init() {
        AddonContextEvent event = new AddonContextEvent(this);
        Iterator<AddonContextListener> listeners = deploymentConfiguration
                .getAddonContextListeners();
        while (listeners.hasNext()) {
            AddonContextListener listener = listeners.next();
            listener.contextCreated(event);
            initedListeners.add(listener);
        }
    }

    /**
     * Destroys this context, causing all initialized listeners to be invoked.
     * <p>
     * This method is not intended to be used by add-ons, but instead by the
     * part of the framework that created this context object.
     */
    public void destroy() {
        AddonContextEvent event = new AddonContextEvent(this);
        for (AddonContextListener listener : initedListeners) {
            listener.contextDestoryed(event);
        }
    }

    /**
     * Shorthand for adding a bootstrap listener that will be added to every new
     * application.
     * 
     * @see #addApplicationStartedListener(ApplicationStartedListener)
     * @see VaadinSession#addBootstrapListener(BootstrapListener)
     * 
     * @param listener
     *            the bootstrap listener that should be added to all new
     *            applications.
     */
    public void addBootstrapListener(BootstrapListener listener) {
        bootstrapListeners.add(listener);
    }

    /**
     * Fires an {@link ApplicationStartedEvent} to all registered listeners.
     * This method is not intended to be used by add-ons, but instead by the
     * part of the framework that creates new Application instances.
     * 
     * @see #addApplicationStartedListener(ApplicationStartedListener)
     * 
     * @param application
     *            the newly started application
     */
    public void fireApplicationStarted(VaadinSession application) {
        eventRouter.fireEvent(new ApplicationStartedEvent(this, application));
        for (BootstrapListener l : bootstrapListeners) {
            application.addBootstrapListener(l);
        }
    }

    /**
     * Adds a listener that will be notified any time a new {@link VaadinSession}
     * instance is started or more precisely directly after
     * {@link VaadinSession#init()} has been invoked.
     * 
     * @param applicationStartListener
     *            the application start listener that should be added
     */
    public void addApplicationStartedListener(
            ApplicationStartedListener applicationStartListener) {
        eventRouter.addListener(ApplicationStartedEvent.class,
                applicationStartListener, APPLICATION_STARTED_METHOD);
    }

    /**
     * Removes an application start listener.
     * 
     * @see #addApplicationStartedListener(ApplicationStartedListener)
     * 
     * @param applicationStartListener
     *            the application start listener to remove
     */
    public void removeApplicationStartedListener(
            ApplicationStartedListener applicationStartListener) {
        eventRouter.removeListener(ApplicationStartedEvent.class,
                applicationStartListener, APPLICATION_STARTED_METHOD);
    }

}
