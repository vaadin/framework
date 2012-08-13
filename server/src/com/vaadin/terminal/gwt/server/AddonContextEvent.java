/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;

public class AddonContextEvent extends EventObject {

    public AddonContextEvent(AddonContext source) {
        super(source);
    }

    public AddonContext getAddonContext() {
        return (AddonContext) getSource();
    }

}
