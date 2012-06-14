/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper.JavaScriptConnectorState;

public class JavaScriptComponentState extends ComponentState implements
        JavaScriptConnectorState {

    private Set<String> callbackNames = new HashSet<String>();

    public Set<String> getCallbackNames() {
        return callbackNames;
    }

    public void setCallbackNames(Set<String> callbackNames) {
        this.callbackNames = callbackNames;
    }

}
