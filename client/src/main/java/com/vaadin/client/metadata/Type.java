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
package com.vaadin.client.metadata;

import java.util.Collection;

import com.google.gwt.core.client.JsArrayString;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.JSONSerializer;

public class Type {
    private final String name;
    private final Type[] parameterTypes;
    private final String signature;

    public Type(Class<?> clazz) {
        this(clazz.getName(), null);
    }

    public Type(String baseTypeName, Type[] parameterTypes) {
        name = baseTypeName;
        this.parameterTypes = parameterTypes;
        // Cache derived signature value
        signature = calculateSignature(name, parameterTypes);
    }

    public String getBaseTypeName() {
        return name;
    }

    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    public Object createInstance() throws NoDataException {
        Invoker invoker = TypeDataStore.getConstructor(this);
        Object ret = invoker.invoke(null);
        if (ret instanceof ServerConnector) {
            ConnectorBundleLoader.get().cval(name);
        }
        return ret;
    }

    public Method getMethod(String name) {
        return new Method(this, name);
    }

    /**
     * @return
     * @throws NoDataException
     *
     * @deprecated As of 7.0.1, use {@link #getPropertiesAsArray()} instead for
     *             improved performance
     */
    @Deprecated
    public Collection<Property> getProperties() throws NoDataException {
        return TypeDataStore.getProperties(this);
    }

    public JsArrayObject<Property> getPropertiesAsArray()
            throws NoDataException {
        return TypeDataStore.getPropertiesAsArray(this);
    }

    public Property getProperty(String propertyName) {
        return new Property(this, propertyName);
    }

    private static String calculateSignature(String name, Type[] parameterTypes) {
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

    /**
     * The unique signature used to identify this type. The structure of the
     * returned string may change without notice and should not be used for any
     * other purpose than identification. The signature is currently based on
     * the fully qualified name of the type.
     *
     * @return the unique signature of this type
     */
    public String getSignature() {
        return signature;
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

    public JsArrayString getDelegateToWidgetProperties() {
        return TypeDataStore.getDelegateToWidgetProperites(this);
    }

}
