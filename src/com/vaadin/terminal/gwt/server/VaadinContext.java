/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.lang.reflect.Method;
import java.util.Iterator;

import com.vaadin.event.EventRouter;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.tools.ReflectTools;

public class VaadinContext {
    private static final Method BOOTSTRAP_FRAGMENT_METHOD = ReflectTools
            .findMethod(BootstrapListener.class, "modifyBootstrapFragment",
                    BootstrapFragmentResponse.class);
    private static final Method BOOTSTRAP_PAGE_METHOD = ReflectTools
            .findMethod(BootstrapListener.class, "modifyBootstrapPage",
                    BootstrapPageResponse.class);

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

    public void addBootstrapListener(BootstrapListener listener) {
        eventRouter.addListener(BootstrapFragmentResponse.class, listener,
                BOOTSTRAP_FRAGMENT_METHOD);
        eventRouter.addListener(BootstrapPageResponse.class, listener,
                BOOTSTRAP_PAGE_METHOD);
    }

    public void fireModifyBootstrapEvent(BootstrapResponse bootstrapResponse) {
        eventRouter.fireEvent(bootstrapResponse);
    }

}
