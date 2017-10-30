package com.vaadin.tests.widgetset.client.minitutorials.v7a3;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7a3.CapsLockWarning;

@Connect(CapsLockWarning.class)
public class CapsLockWarningConnector extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        final Widget passwordWidget = ((ComponentConnector) target).getWidget();

        final VOverlay warning = new VOverlay();
        warning.setOwner(passwordWidget);
        warning.add(new HTML("Caps Lock is enabled!"));

        passwordWidget.addDomHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (isEnabled() && isCapsLockOn(event)) {
                    warning.showRelativeTo(passwordWidget);
                } else {
                    warning.hide();
                }
            }
        }, KeyPressEvent.getType());
    }

    private boolean isCapsLockOn(KeyPressEvent e) {
        return e.isShiftKeyDown() ^ Character.isUpperCase(e.getCharCode());
    }
}
