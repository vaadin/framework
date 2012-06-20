/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.extensions.javascriptmanager;

import com.vaadin.terminal.gwt.client.communication.ClientRpc;

public interface ExecuteJavaScriptRpc extends ClientRpc {
    public void executeJavaScript(String script);
}
