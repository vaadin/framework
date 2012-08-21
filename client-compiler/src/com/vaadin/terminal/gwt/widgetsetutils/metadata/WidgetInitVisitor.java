/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

public class WidgetInitVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) {
        if (ConnectorBundle.isConnectedComponentConnector(type)) {
            JMethod getWidget = findInheritedMethod(type, "getWidget");
            bundle.setNeedsReturnType(type, getWidget);

            JType widgetType = getWidget.getReturnType();
            bundle.setNeedsGwtConstructor(widgetType.isClass());
        }
    }
}
