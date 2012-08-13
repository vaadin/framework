/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;

import com.vaadin.Application;

public class ApplicationStartedEvent extends EventObject {
    private final Application application;

    public ApplicationStartedEvent(VaadinContext context,
            Application application) {
        super(context);
        this.application = application;
    }

    public VaadinContext getContext() {
        return (VaadinContext) getSource();
    }

    public Application getApplication() {
        return application;
    }

}
