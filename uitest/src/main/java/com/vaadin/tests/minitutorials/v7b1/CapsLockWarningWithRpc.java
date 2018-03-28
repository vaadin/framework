package com.vaadin.tests.minitutorials.v7b1;

import com.vaadin.server.AbstractExtension;
import com.vaadin.tests.widgetset.client.minitutorials.v7b1.CapsLockWarningRpc;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;

public class CapsLockWarningWithRpc extends AbstractExtension {
    public CapsLockWarningWithRpc() {
        registerRpc(new CapsLockWarningRpc() {
            @Override
            public void isCapsLockEnabled(boolean isCapsLockEnabled) {
                Notification.show("Caps Lock was "
                        + (isCapsLockEnabled ? "enabled" : "disabled"));
            }
        });
    }

    public void extend(PasswordField field) {
        super.extend(field);
    }
}
