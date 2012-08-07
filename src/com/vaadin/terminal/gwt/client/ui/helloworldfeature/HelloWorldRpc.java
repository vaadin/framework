/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.helloworldfeature;

import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface HelloWorldRpc extends ServerRpc {
    public void onMessageSent(String message);
}
