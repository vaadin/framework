/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventListener;

public interface AddonContextListener extends EventListener {
    public void contextCreated(AddonContextEvent event);

    public void contextDestoryed(AddonContextEvent event);
}
