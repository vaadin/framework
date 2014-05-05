package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface MyComponentServerRpc extends ServerRpc {

    public void clicked(MouseEventDetails mouseDetails);

}
