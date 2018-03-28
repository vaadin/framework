package com.vaadin.tests.widgetset.client.minitutorials.v7b1;

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

public interface CapsLockWarningRpc extends ServerRpc {
    @Delayed(lastOnly = true)
    public void isCapsLockEnabled(boolean isCapsLockEnabled);
}
