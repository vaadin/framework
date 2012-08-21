/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public abstract class TypeVisitor {
    public void init(TypeOracle oracle) throws NotFoundException {
        // Default does nothing
    }

    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) {
        // Default does nothing
    }

    public void visitClientRpc(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) {
        // Default does nothing
    }

    public void visitServerRpc(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) {
        // Default does nothing
    }

    protected JMethod findInheritedMethod(JClassType type, String methodName,
            JType... params) {

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

        return null;
    }

}
