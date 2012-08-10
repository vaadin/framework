/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventListener;

public interface VaadinContextListener extends EventListener {
    public void contextCreated(VaadinContextEvent event);

    public void contextDestoryed(VaadinContextEvent event);
}
