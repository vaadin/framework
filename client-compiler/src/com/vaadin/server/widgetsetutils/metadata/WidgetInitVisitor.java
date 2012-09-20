/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.server.widgetsetutils.metadata;

import java.util.Collection;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.annotations.DelegateToWidget;

public class WidgetInitVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        if (ConnectorBundle.isConnectedComponentConnector(type)) {
            // The class in which createWidget is implemented
            JClassType createWidgetClass = findInheritedMethod(type,
                    "createWidget").getEnclosingType();

            JMethod getWidget = findInheritedMethod(type, "getWidget");
            JClassType widgetType = getWidget.getReturnType().isClass();

            // Needs GWT constructor if createWidget is not overridden
            if (createWidgetClass.getQualifiedSourceName().equals(
                    AbstractComponentConnector.class.getCanonicalName())) {
                bundle.setNeedsGwtConstructor(widgetType);

                // Also needs widget type to find the right GWT constructor
                bundle.setNeedsReturnType(type, getWidget);
            }

            // Check state properties for @DelegateToWidget
            JMethod getState = findInheritedMethod(type, "getState");
            JClassType stateType = getState.getReturnType().isClass();

            Collection<Property> properties = bundle.getProperties(stateType);
            for (Property property : properties) {
                DelegateToWidget delegateToWidget = property
                        .getAnnotation(DelegateToWidget.class);
                if (delegateToWidget != null) {
                    // Generate meta data required for @DelegateToWidget
                    bundle.setNeedsDelegateToWidget(property);

                    // Find the delegate target method
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

                    // GWT code needs widget type to find the target method
                    bundle.setNeedsReturnType(type, getWidget);
                }
            }

        }
    }
}
