/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.Iterator;

import com.vaadin.event.EventRouter;
import com.vaadin.terminal.DeploymentConfiguration;

public class VaadinContext {
    private final DeploymentConfiguration deploymentConfiguration;

    private final EventRouter eventRouter = new EventRouter();

    public VaadinContext(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;
        deploymentConfiguration.setVaadinContext(this);
    }

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    public void init() {
        VaadinContextEvent event = new VaadinContextEvent(this);
        Iterator<VaadinContextListener> listeners = deploymentConfiguration
                .getContextListeners();
        while (listeners.hasNext()) {
            VaadinContextListener listener = listeners.next();
            listener.contextCreated(event);
        }
    }

    public void destroy() {
        VaadinContextEvent event = new VaadinContextEvent(this);
        Iterator<VaadinContextListener> listeners = deploymentConfiguration
                .getContextListeners();
        while (listeners.hasNext()) {
            VaadinContextListener listener = listeners.next();
            listener.contextDestoryed(event);
        }
    }

}
