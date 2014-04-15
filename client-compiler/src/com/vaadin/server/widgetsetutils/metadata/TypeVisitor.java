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
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public abstract class TypeVisitor {
    public void init(TypeOracle oracle) throws NotFoundException {
        // Default does nothing
    }

    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        // Default does nothing
    }

    public void visitClientRpc(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        // Default does nothing
    }

    public void visitServerRpc(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        // Default does nothing
    }

    protected JMethod findInheritedMethod(JClassType type, String methodName,
            JType... params) {

        JClassType currentType = type;
        while (currentType != null) {
            JMethod method = currentType.findMethod(methodName, params);
            if (method != null) {
                return method;
            }
            currentType = currentType.getSuperclass();
        }

        JClassType[] interfaces = type.getImplementedInterfaces();
        for (JClassType iface : interfaces) {
            JMethod method = iface.findMethod(methodName, params);
            if (method != null) {
                return method;
            }
        }

        return null;
    }

}
