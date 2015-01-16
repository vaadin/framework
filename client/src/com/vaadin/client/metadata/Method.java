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

import com.vaadin.shared.annotations.NoLayout;

public class Method {

    private final Type type;
    private final String name;

    public Method(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() throws NoDataException {
        return TypeDataStore.getReturnType(this);
    }

    public void invoke(Object target, Object... params) throws NoDataException {
        TypeDataStore.getInvoker(this).invoke(target, params);
    }

    /**
     * The unique signature used to identify this method. The structure of the
     * returned string may change without notice and should not be used for any
     * other purpose than identification. The signature is currently based on
     * the declaring type's signature and the method's name.
     * 
     * @return the unique signature of this method
     */
    public String getSignature() {
        return type.getSignature() + "." + name;
    }

    /**
     * Gets the string that is internally used when looking up generated support
     * code for this method. This is the same as {@link #getSignature()}, but
     * without any type parameters.
     * 
     * @return the string to use for looking up generated support code
     * 
     * @since 7.2
     */
    public String getLookupKey() {
        return type.getBaseTypeName() + "." + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Method) {
            Method other = (Method) obj;
            return other.getSignature().equals(getSignature());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return getSignature();
    }

    @Override
    public int hashCode() {
        return getSignature().hashCode();
    }

    public Type[] getParameterTypes() throws NoDataException {
        return TypeDataStore.getParamTypes(this);
    }

    public boolean isDelayed() {
        return TypeDataStore.isDelayed(this);
    }

    public boolean isLastOnly() {
        return TypeDataStore.isLastOnly(this);
    }

    /**
     * Checks whether this method is annotated with {@link NoLayout}.
     * 
     * @since 7.4
     * 
     * @return <code>true</code> if this method has a NoLayout annotation;
     *         otherwise <code>false</code>
     */
    public boolean isNoLayout() {
        return TypeDataStore.isNoLayoutRpcMethod(this);
    }

}
