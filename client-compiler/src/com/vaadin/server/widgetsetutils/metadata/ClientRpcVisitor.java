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
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.vaadin.client.metadata.TypeDataStore.MethodAttribute;
import com.vaadin.shared.annotations.NoLayout;

public class ClientRpcVisitor extends TypeVisitor {
    @Override
    public void visitClientRpc(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        checkGenericType(logger, type);
        Set<? extends JClassType> hierarchy = type
                .getFlattenedSupertypeHierarchy();
        for (JClassType subType : hierarchy) {
            JMethod[] methods = subType.getMethods();
            for (JMethod method : methods) {
                checkReturnType(logger, method);

                bundle.setNeedsInvoker(type, method);
                bundle.setNeedsParamTypes(type, method);
                if (method.getAnnotation(NoLayout.class) != null) {
                    bundle.setMethodAttribute(type, method,
                            MethodAttribute.NO_LAYOUT);
                }

                JType[] parameterTypes = method.getParameterTypes();
                for (JType paramType : parameterTypes) {
                    bundle.setNeedsSerialize(paramType);
                }
            }
        }
    }

    public static void checkGenericType(TreeLogger logger, JClassType type)
            throws UnableToCompleteException {
        if (type.isGenericType() != null) {
            logger.log(Type.ERROR,
                    "Type " + type.getParameterizedQualifiedSourceName()
                            + "is parameterizied generic. RPC proxy "
                            + "for parameterizied types is not supported.");
            throw new UnableToCompleteException();
        }
    }

    public static void checkReturnType(TreeLogger logger, JMethod method)
            throws UnableToCompleteException {
        if (!method.getReturnType().getQualifiedSourceName().equals("void")) {
            logger.log(
                    Type.ERROR,
                    "The method "
                            + method.getEnclosingType()
                                    .getQualifiedSourceName()
                            + "."
                            + method.getName()
                            + " returns "
                            + method.getReturnType().getQualifiedSourceName()
                            + " but only void is supported for methods in RPC interfaces.");
            throw new UnableToCompleteException();
        }
    }
}
