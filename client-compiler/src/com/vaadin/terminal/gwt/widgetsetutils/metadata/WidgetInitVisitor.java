/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import java.util.Collection;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;

public class WidgetInitVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
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

            JClassType widgetType = getWidget.getReturnType().isClass();
            bundle.setNeedsGwtConstructor(widgetType);

            JMethod getState = findInheritedMethod(type, "getState");
            JClassType stateType = getState.getReturnType().isClass();

            Collection<Property> properties = bundle.getProperties(stateType);
            for (Property property : properties) {
                DelegateToWidget delegateToWidget = property
                        .getAnnotation(DelegateToWidget.class);
                if (delegateToWidget != null) {
                    bundle.setNeedsDelegateToWidget(property);
                    String methodName = DelegateToWidget.Helper
                            .getDelegateTarget(property.getName(),
                                    delegateToWidget.value());
                    JMethod delegatedSetter = findInheritedMethod(widgetType,
                            methodName, property.getPropertyType());
                    if (delegatedSetter == null) {
                        logger.log(
                                Type.ERROR,
                                widgetType.getName()
                                        + "."
                                        + methodName
                                        + "("
                                        + property.getPropertyType()
                                                .getSimpleSourceName()
                                        + ") required by @DelegateToWidget for "
                                        + stateType.getName() + "."
                                        + property.getName()
                                        + " can not be found.");
                        throw new UnableToCompleteException();
                    }
                    bundle.setNeedsInvoker(widgetType, delegatedSetter);
                }
            }
        }
    }
}
