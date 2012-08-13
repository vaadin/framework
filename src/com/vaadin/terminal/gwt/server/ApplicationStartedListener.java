/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventListener;

public interface ApplicationStartedListener extends EventListener {
    public void applicationStarted(ApplicationStartedEvent event);
}
