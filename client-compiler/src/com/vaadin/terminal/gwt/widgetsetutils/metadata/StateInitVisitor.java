/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

public class StateInitVisitor extends TypeVisitor {
    @Override
    public void visit(JClassType type, ConnectorBundle bundle)
            throws NotFoundException {
        if (isConnectedConnector(type)) {
            JMethod getState = getInheritedMethod(type, "getState");
            bundle.setNeedsReturnType(type, getState);

            JType stateType = getState.getReturnType();
            bundle.setNeedsGwtConstructor(stateType.isClass());
        }
    }

}
