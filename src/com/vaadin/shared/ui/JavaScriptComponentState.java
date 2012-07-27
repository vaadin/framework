/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.JavaScriptConnectorState;

public class JavaScriptComponentState extends ComponentState implements
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
