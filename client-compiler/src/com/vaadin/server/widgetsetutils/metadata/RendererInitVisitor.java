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
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.vaadin.client.ui.grid.renderers.AbstractRendererConnector;

/**
 * Generates type data for renderer connectors. Specifically, stores the return
 * type of the overridden {@link AbstractRendererConnector#getRenderer()
 * getRenderer} method to enable automatic creation of an instance of the proper
 * renderer type.
 * 
 * @see WidgetInitVisitor
 * 
 * @since
 * @author Vaadin Ltd
 */
public class RendererInitVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {

        if (ConnectorBundle.isConnectedRendererConnector(type)) {

            // The class in which createRenderer is implemented
            JClassType createRendererClass = ConnectorBundle
                    .findInheritedMethod(type, "createRenderer")
                    .getEnclosingType();

            JMethod getRenderer = ConnectorBundle.findInheritedMethod(type,
                    "getRenderer");
            JClassType rendererType = getRenderer.getReturnType().isClass();

            // Needs GWT constructor if createRenderer is not overridden
            if (createRendererClass.getQualifiedSourceName().equals(
                    AbstractRendererConnector.class.getCanonicalName())) {

                bundle.setNeedsGwtConstructor(rendererType);

                // Also needs renderer type to find the right GWT constructor
                bundle.setNeedsReturnType(type, getRenderer);
            }
        }
    }
}
