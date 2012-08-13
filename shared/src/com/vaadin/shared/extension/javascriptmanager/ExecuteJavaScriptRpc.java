/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared.extension.javascriptmanager;

import com.vaadin.shared.communication.ClientRpc;

public interface ExecuteJavaScriptRpc extends ClientRpc {
    public void executeJavaScript(String script);
}
