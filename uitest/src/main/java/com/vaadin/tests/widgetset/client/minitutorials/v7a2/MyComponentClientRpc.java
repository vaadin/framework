package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.vaadin.shared.communication.ClientRpc;

public interface MyComponentClientRpc extends ClientRpc {

    public void alert(String message);

}
