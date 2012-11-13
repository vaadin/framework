package com.vaadin.client.ui.popupview;

import com.vaadin.shared.communication.ServerRpc;

public interface PopupViewServerRpc extends ServerRpc {

    public void setPopupVisibility(Boolean visible);

}
