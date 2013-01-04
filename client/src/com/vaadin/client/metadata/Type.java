/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.client.metadata;

import java.util.Collection;

import com.vaadin.client.communication.JSONSerializer;

public class Type {
    private final String name;
    private final Type[] parameterTypes;

    public Type(Class<?> clazz) {
        name = clazz.getName();
        parameterTypes = null;
    }

    public Type(String baseTypeName, Type[] parameterTypes) {
        name = baseTypeName;
        this.parameterTypes = parameterTypes;
    }

    public String getBaseTypeName() {
        return name;
    }

    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    public Object createInstance() throws NoDataException {
        Invoker invoker = TypeDataStore.getConstructor(this);
        return invoker.invoke(null);
    }

    public Method getMethod(String name) {
        return new Method(this, name);
    }

    public Collection<Property> getProperties() throws NoDataException {
        return TypeDataStore.getProperties(this);
    }

    public Property getProperty(String propertyName) {
        return new Property(this, propertyName);
    }

    public String getSignature() {
        String string = name;
        if (parameterTypes != null && parameterTypes.length != 0) {
            string += '<';
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i != 0) {
                    string += ',';
                }
                string += parameterTypes[i].toString();
            }
            string += '>';
        }

        return string;
    }

    @Override
    public String toString() {
        return getSignature();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Type) {
            Type other = (Type) obj;
            return other.getSignature().equals(getSignature());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getSignature().hashCode();
    }

    public Object createProxy(InvokationHandler invokationHandler)
            throws NoDataException {
        return TypeDataStore.get().getProxyHandler(this)
                .createProxy(invokationHandler);
    }

    public JSONSerializer<?> findSerializer() {
        return TypeDataStore.findSerializer(this);
    }

    public boolean hasProperties() {
        return TypeDataStore.hasProperties(this);
    }

}
