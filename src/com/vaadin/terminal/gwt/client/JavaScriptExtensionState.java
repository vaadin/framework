/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper.JavaScriptConnectorState;
import com.vaadin.terminal.gwt.client.communication.SharedState;

public class JavaScriptExtensionState extends SharedState implements
        JavaScriptConnectorState {

    private Set<String> callbackNames = new HashSet<String>();

    public Set<String> getCallbackNames() {
        return callbackNames;
    }

    public void setCallbackNames(Set<String> callbackNames) {
        this.callbackNames = callbackNames;
    }

}
