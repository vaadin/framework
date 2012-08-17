/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

public class WidgetInitVisitor extends TypeVisitor {

    @Override
    public void visit(JClassType type, ConnectorBundle bundle)
            throws NotFoundException {
        if (isConnectedComponentConnector(type)) {
            JMethod getWidget = getInheritedMethod(type, "getWidget");
            bundle.setNeedsReturnType(type, getWidget);

            JType widgetType = getWidget.getReturnType();
            bundle.setNeedsGwtConstructor(widgetType.isClass());
        }
    }
}
