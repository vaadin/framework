package com.vaadin.tests.widgetset.client.minitutorials.v7b1;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7b1.CapsLockWarningWithRpc;

@Connect(CapsLockWarningWithRpc.class)
public class CapsLockWarningWithRpcConnector
        extends AbstractExtensionConnector {

    private CapsLockWarningRpc rpc = getRpcProxy(CapsLockWarningRpc.class);

    @Override
    protected void extend(ServerConnector target) {
        final Widget passwordWidget = ((ComponentConnector) target).getWidget();

        final VOverlay warning = new VOverlay();
        warning.setOwner(passwordWidget);
        warning.add(new HTML("Caps Lock is enabled!"));

        passwordWidget.addDomHandler(event -> {
            if (isEnabled() && isCapsLockOn(event)) {
                warning.showRelativeTo(passwordWidget);
                // Added to send message to the server
                rpc.isCapsLockEnabled(true);
            } else {
                warning.hide();
                // Added to send message to the server
                rpc.isCapsLockEnabled(false);
            }
        }, KeyPressEvent.getType());
    }

    private boolean isCapsLockOn(KeyPressEvent e) {
        return e.isShiftKeyDown() ^ Character.isUpperCase(e.getCharCode());
    }
}
