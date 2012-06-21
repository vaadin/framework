package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface MyComponentServerRpc extends ServerRpc {

    public void clicked(MouseEventDetails mouseDetails);

}