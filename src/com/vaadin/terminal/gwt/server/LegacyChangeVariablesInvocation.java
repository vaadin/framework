/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;

public class LegacyChangeVariablesInvocation extends MethodInvocation {
    private Map<String, Object> variableChanges = new HashMap<String, Object>();

    public LegacyChangeVariablesInvocation(String connectorId,
            String variableName, Object value) {
        super(connectorId, ApplicationConnection.UPDATE_VARIABLE_INTERFACE,
                ApplicationConnection.UPDATE_VARIABLE_METHOD);
        setVariableChange(variableName, value);
    }

    public static boolean isLegacyVariableChange(String interfaceName,
            String methodName) {
        return ApplicationConnection.UPDATE_VARIABLE_METHOD
                .equals(interfaceName)
                && ApplicationConnection.UPDATE_VARIABLE_METHOD
                        .equals(methodName);
    }

    public void setVariableChange(String name, Object value) {
        variableChanges.put(name, value);
    }

    public Map<String, Object> getVariableChanges() {
        return variableChanges;
    }

}
