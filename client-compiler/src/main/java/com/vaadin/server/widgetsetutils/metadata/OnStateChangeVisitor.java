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
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;

/**
 * Visits Connector classes and check for methods with @OnStateChange
 * annotations.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class OnStateChangeVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        Connect connectAnnotation = type.getAnnotation(Connect.class);
        if (connectAnnotation != null) {
            // Find all the annotated methods in all the superclasses
            JClassType connector = type;
            while (connector != null) {
                for (JMethod method : connector.getMethods()) {
                    if (method.getAnnotation(OnStateChange.class) != null) {
                        bundle.setNeedsInvoker(connector, method);
                        bundle.setNeedsOnStateChangeHandler(type, method);
                    }
                }

                connector = connector.getSuperclass();
            }
        }
    }

}
