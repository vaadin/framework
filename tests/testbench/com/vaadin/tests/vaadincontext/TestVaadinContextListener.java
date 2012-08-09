/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.vaadincontext;

import com.vaadin.terminal.gwt.server.BootstrapListener;
import com.vaadin.terminal.gwt.server.BootstrapResponse;
import com.vaadin.terminal.gwt.server.VaadinContextEvent;
import com.vaadin.terminal.gwt.server.VaadinContextListener;
import com.vaadin.ui.Root;

public class TestVaadinContextListener implements VaadinContextListener {
    @Override
    public void contextCreated(VaadinContextEvent event) {
        event.getVaadinContext().addBootstrapListener(new BootstrapListener() {
            @Override
            public void modifyBootstrap(BootstrapResponse response) {
                Root root = response.getRoot();
                if (root != null && root.getClass() == BoostrapModifyRoot.class) {
                    response.getApplicationTag().before(
                            "<h1>This is a heading</h1>");
                }
            }
        });
    }

    @Override
    public void contextDestoryed(VaadinContextEvent event) {
        // Nothing to do
    }

}
