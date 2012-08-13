/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.Application;
import com.vaadin.event.EventRouter;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.tools.ReflectTools;

public class AddonContext {
    private static final Method APPLICATION_STARTED_METHOD = ReflectTools
            .findMethod(ApplicationStartedListener.class, "applicationStarted",
                    ApplicationStartedEvent.class);

    private final DeploymentConfiguration deploymentConfiguration;

    private final EventRouter eventRouter = new EventRouter();

    private List<BootstrapListener> bootstrapListeners = new ArrayList<BootstrapListener>();

    public AddonContext(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;
        deploymentConfiguration.setAddonContext(this);
    }

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    public void init() {
        AddonContextEvent event = new AddonContextEvent(this);
        Iterator<AddonContextListener> listeners = deploymentConfiguration
                .getAddonContextListeners();
        while (listeners.hasNext()) {
            AddonContextListener listener = listeners.next();
            listener.contextCreated(event);
        }
    }

    public void destroy() {
        AddonContextEvent event = new AddonContextEvent(this);
        Iterator<AddonContextListener> listeners = deploymentConfiguration
                .getAddonContextListeners();
        while (listeners.hasNext()) {
            AddonContextListener listener = listeners.next();
            listener.contextDestoryed(event);
        }
    }

    public void addBootstrapListener(BootstrapListener listener) {
        bootstrapListeners.add(listener);
    }

    public void applicationStarted(Application application) {
        eventRouter.fireEvent(new ApplicationStartedEvent(this, application));
        for (BootstrapListener l : bootstrapListeners) {
            application.addBootstrapListener(l);
        }
    }

    public void addApplicationStartedListener(
            ApplicationStartedListener applicationStartListener) {
        eventRouter.addListener(ApplicationStartedEvent.class,
                applicationStartListener, APPLICATION_STARTED_METHOD);
    }

    public void removeApplicationStartedListener(
            ApplicationStartedListener applicationStartListener) {
        eventRouter.removeListener(ApplicationStartedEvent.class,
                applicationStartListener, APPLICATION_STARTED_METHOD);
    }

}
