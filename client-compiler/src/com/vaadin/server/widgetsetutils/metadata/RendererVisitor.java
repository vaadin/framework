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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.vaadin.client.connectors.AbstractRendererConnector;

/**
 * Generates type data for renderer connectors.
 * <ul>
 * <li>Stores the return type of the overridden
 * {@link AbstractRendererConnector#getRenderer() getRenderer} method to enable
 * automatic creation of an instance of the proper renderer type.
 * <li>Stores the presentation type of the connector to enable the
 * {@link AbstractRendererConnector#decode(elemental.json.JsonValue) decode}
 * method to work without having to implement a "getPresentationType" method.
 * </ul>
 * 
 * @see WidgetInitVisitor
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class RendererVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        if (ConnectorBundle.isConnectedRendererConnector(type)) {
            doRendererType(logger, type, bundle);
            doPresentationType(logger, type, bundle);
        }
    }

    private static void doRendererType(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) {
        // The class in which createRenderer is implemented
        JClassType createRendererClass = ConnectorBundle.findInheritedMethod(
                type, "createRenderer").getEnclosingType();

        // Needs GWT constructor if createRenderer is not overridden
        if (createRendererClass.getQualifiedSourceName().equals(
                AbstractRendererConnector.class.getCanonicalName())) {

            JMethod getRenderer = ConnectorBundle.findInheritedMethod(type,
                    "getRenderer");
            JClassType rendererType = getRenderer.getReturnType().isClass();

            bundle.setNeedsGwtConstructor(rendererType);

            // Also needs renderer type to find the right GWT constructor
            bundle.setNeedsReturnType(type, getRenderer);

            logger.log(Type.DEBUG, "Renderer type of " + type + " is "
                    + rendererType);
        }
    }

    private void doPresentationType(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        JType presentationType = getPresentationType(type, logger);
        bundle.setPresentationType(type, presentationType);

        bundle.setNeedsSerialize(presentationType);

        logger.log(Type.DEBUG, "Presentation type of " + type + " is "
                + presentationType);
    }

    private static JType getPresentationType(JClassType type, TreeLogger logger)
            throws UnableToCompleteException {
        JClassType originalType = type;
        while (type != null) {
            if (type.getQualifiedBinaryName().equals(
                    AbstractRendererConnector.class.getName())) {
                JParameterizedType parameterized = type.isParameterized();
                if (parameterized == null) {
                    logger.log(
                            Type.ERROR,
                            type.getQualifiedSourceName()
                                    + " must define the generic parameter of the inherited "
                                    + AbstractRendererConnector.class
                                            .getSimpleName());
                    throw new UnableToCompleteException();
                }
                return parameterized.getTypeArgs()[0];
            }
            type = type.getSuperclass();
        }
        throw new IllegalArgumentException("The type "
                + originalType.getQualifiedSourceName() + " does not extend "
                + AbstractRendererConnector.class.getName());
    }
}
