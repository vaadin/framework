package com.vaadin.shared.ui.popupview;

import com.vaadin.shared.communication.ServerRpc;

public interface PopupViewServerRpc extends ServerRpc {

    public void setPopupVisibility(boolean visible);

}
