/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper.JavaScriptConnectorState;
import com.vaadin.terminal.gwt.client.communication.SharedState;

public class JavaScriptExtensionState extends SharedState implements
        JavaScriptConnectorState {

    private Set<String> callbackNames = new HashSet<String>();
    private Map<String, Set<String>> rpcInterfaces = new HashMap<String, Set<String>>();

    @Override
    public Set<String> getCallbackNames() {
        return callbackNames;
    }

    public void setCallbackNames(Set<String> callbackNames) {
        this.callbackNames = callbackNames;
    }

    @Override
    public Map<String, Set<String>> getRpcInterfaces() {
        return rpcInterfaces;
    }

    public void setRpcInterfaces(Map<String, Set<String>> rpcInterfaces) {
        this.rpcInterfaces = rpcInterfaces;
    }
}
