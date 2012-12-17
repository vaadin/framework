/*
 * Copyright 2011 Vaadin Ltd.
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

import java.util.Set;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

public class ServerRpcVisitor extends TypeVisitor {
    @Override
    public void visitServerRpc(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        bundle.setNeedsProxySupport(type);

        Set<? extends JClassType> superTypes = type
                .getFlattenedSupertypeHierarchy();
        for (JClassType subType : superTypes) {
            if (subType.isInterface() != null) {
                JMethod[] methods = subType.getMethods();
                for (JMethod method : methods) {
                    ClientRpcVisitor.checkReturnType(logger, method);
                    bundle.setNeedsDelayedInfo(type, method);
                    bundle.setNeedsParamTypes(subType, method);

                    JType[] parameterTypes = method.getParameterTypes();
                    for (JType paramType : parameterTypes) {
                        bundle.setNeedsSerialize(paramType);
                    }
                }
            }
        }
    }
}
