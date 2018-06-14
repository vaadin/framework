package com.vaadin.tests.widgetset.client.contextmenu;

import com.vaadin.shared.communication.ClientRpc;

public interface ContextMenuClientRpc extends ClientRpc {
    /**
     * Sends request to client widget to open context menu to given position.
     *
     * @param x
     * @param y
     */
    public void showContextMenu(int x, int y);
}
