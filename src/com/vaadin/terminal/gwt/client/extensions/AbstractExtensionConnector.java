/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.extensions;

import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.ui.AbstractConnector;

public abstract class AbstractExtensionConnector extends AbstractConnector {
    boolean hasBeenAttached = false;

    @Override
    public void setParent(ServerConnector parent) {
        ServerConnector oldParent = getParent();
        if (oldParent == parent) {
            // Nothing to do
            return;
        }
        if (hasBeenAttached && parent != null) {
            throw new IllegalStateException(
                    "An extension can not be moved from one parent to another.");
        }

        super.setParent(parent);

        if (parent != null) {
            extend(parent);
            hasBeenAttached = true;
        }
    }

    protected void extend(ServerConnector target) {
        // Default does nothing
    }
}
