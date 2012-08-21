/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;

public class WidgetInitVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) {
        if (ConnectorBundle.isConnectedComponentConnector(type)) {
            JClassType createWidgetClass = findInheritedMethod(type,
                    "createWidget").getEnclosingType();
            boolean createWidgetOverridden = !createWidgetClass
                    .getQualifiedSourceName()
                    .equals(AbstractComponentConnector.class.getCanonicalName());
            if (createWidgetOverridden) {
                // Don't generate if createWidget is already overridden
                return;
            }

            JMethod getWidget = findInheritedMethod(type, "getWidget");
            bundle.setNeedsReturnType(type, getWidget);

            JType widgetType = getWidget.getReturnType();
            bundle.setNeedsGwtConstructor(widgetType.isClass());
        }
    }
}
