/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventListener;

public interface BootstrapListener extends EventListener {
    public void modifyBootstrapFragment(BootstrapFragmentResponse response);

    public void modifyBootstrapPage(BootstrapPageResponse response);
}
