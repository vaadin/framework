/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ServerConnector;

public abstract class TypeVisitor {
    private JClassType serverConnector;
    private JClassType componentConnector;

    public void init(TypeOracle oracle) throws NotFoundException {
        serverConnector = oracle.getType(ServerConnector.class.getName());
        componentConnector = oracle.getType(ComponentConnector.class.getName());
    }

    public abstract void visit(JClassType type, ConnectorBundle bundle)
            throws NotFoundException;

    protected boolean isConnectedConnector(JClassType type) {
        return serverConnector.isAssignableFrom(type)
                && type.isAnnotationPresent(Connect.class);
    }

    protected boolean isConnectedComponentConnector(JClassType type) {
        return componentConnector.isAssignableFrom(type)
                && type.isAnnotationPresent(Connect.class);
    }

    protected JMethod getInheritedMethod(JClassType type, String methodName,
            JType... params) throws NotFoundException {

        JClassType currentType = type;
        while (currentType != null) {
            JMethod method = currentType.findMethod(methodName, params);
            if (method != null) {
                return method;
            }
            currentType = currentType.getSuperclass();
        }

        JClassType[] interfaces = type.getImplementedInterfaces();
        for (JClassType iface : interfaces) {
            JMethod method = iface.findMethod(methodName, params);
            if (method != null) {
                return method;
            }
        }

        throw new NotFoundException(methodName + " not found in " + type);
    }
}
