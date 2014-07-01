/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
            JClassType createWidgetClass = ConnectorBundle.findInheritedMethod(
                    type, "createWidget").getEnclosingType();

            JMethod getWidget = ConnectorBundle.findInheritedMethod(type,
                    "getWidget");
            JClassType widgetType = getWidget.getReturnType().isClass();

            // Needs GWT constructor if createWidget is not overridden
            if (createWidgetClass.getQualifiedSourceName().equals(
                    AbstractComponentConnector.class.getCanonicalName())) {
                bundle.setNeedsGwtConstructor(widgetType);

                // Also needs widget type to find the right GWT constructor
                bundle.setNeedsReturnType(type, getWidget);
            }

            // Check state properties for @DelegateToWidget
            JMethod getState = ConnectorBundle.findInheritedMethod(type,
                    "getState");
            JClassType stateType = getState.getReturnType().isClass();

            Collection<Property> properties = bundle.getProperties(stateType);
            for (Property property : properties) {
                DelegateToWidget delegateToWidget = property
                        .getAnnotation(DelegateToWidget.class);
                if (delegateToWidget != null) {
                    // Generate meta data required for @DelegateToWidget
                    bundle.setNeedsDelegateToWidget(property, stateType);

                    // Find the delegate target method
                    String methodName = DelegateToWidget.Helper
                            .getDelegateTarget(property.getName(),
                                    delegateToWidget.value());
                    JMethod delegatedSetter = ConnectorBundle
                            .findInheritedMethod(widgetType, methodName,
                                    property.getPropertyType());
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
