/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;

public class VaadinContextEvent extends EventObject {

    public VaadinContextEvent(VaadinContext source) {
        super(source);
    }

    public VaadinContext getVaadinContext() {
        return (VaadinContext) getSource();
    }

}
