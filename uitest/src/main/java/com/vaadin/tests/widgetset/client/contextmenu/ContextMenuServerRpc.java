package com.vaadin.tests.widgetset.client.contextmenu;

import com.vaadin.shared.communication.ServerRpc;

public interface ContextMenuServerRpc extends ServerRpc {
    void itemClicked(int itemId, boolean menuClosed);
}
