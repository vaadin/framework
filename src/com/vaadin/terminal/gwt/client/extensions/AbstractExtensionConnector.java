/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.extensions;

import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.ui.AbstractConnector;

public abstract class AbstractExtensionConnector extends AbstractConnector {
    @Override
    public void setParent(ServerConnector parent) {
        ServerConnector oldParent = getParent();
        if (oldParent != null && oldParent != parent) {
            throw new IllegalStateException(
                    "An extension can not be moved from one parent to another.");
        }

        super.setParent(parent);

        extend(parent);
    }

    protected void extend(ServerConnector target) {
        // Default does nothing
    }
}
