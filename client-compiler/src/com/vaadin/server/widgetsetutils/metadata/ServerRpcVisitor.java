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

import java.util.Set;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.vaadin.client.metadata.TypeDataStore.MethodAttribute;
import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.annotations.Delayed;

public class ServerRpcVisitor extends TypeVisitor {
    @Override
    public void visitServerRpc(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        ClientRpcVisitor.checkGenericType(logger, type);
        bundle.setNeedsProxySupport(type);

        Set<? extends JClassType> superTypes = type
                .getFlattenedSupertypeHierarchy();
        for (JClassType subType : superTypes) {
            if (subType.isInterface() != null) {
                JMethod[] methods = subType.getMethods();
                for (JMethod method : methods) {
                    ClientRpcVisitor.checkReturnType(logger, method);

                    Delayed delayed = method.getAnnotation(Delayed.class);
                    if (delayed != null) {
                        bundle.setMethodAttribute(type, method,
                                MethodAttribute.DELAYED);
                        if (delayed.lastOnly()) {
                            bundle.setMethodAttribute(type, method,
                                    MethodAttribute.LAST_ONLY);
                        }
                    }

                    if (method.getAnnotation(NoLoadingIndicator.class) != null) {
                        bundle.setMethodAttribute(type, method,
                                MethodAttribute.NO_LOADING_INDICATOR);
                    }

                    bundle.setNeedsParamTypes(type, method);

                    JType[] parameterTypes = method.getParameterTypes();
                    for (JType paramType : parameterTypes) {
                        bundle.setNeedsSerialize(paramType);
                    }
                }
            }
        }
    }
}
