/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

public class StateInitVisitor extends TypeVisitor {
    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) {
        JMethod getState = findInheritedMethod(type, "getState");
        bundle.setNeedsReturnType(type, getState);

        bundle.setNeedsSerialize(getState.getReturnType());

        JType stateType = getState.getReturnType();
        bundle.setNeedsGwtConstructor(stateType.isClass());
    }

}
