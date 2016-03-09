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

package com.vaadin.shared.communication;

import java.io.Serializable;
import java.util.Arrays;

import com.vaadin.shared.util.SharedUtil;

/**
 * Information needed by the framework to send an RPC method invocation from the
 * client to the server or vice versa.
 * 
 * @since 7.0
 */
public class MethodInvocation implements Serializable {

    private final String connectorId;
    private final String interfaceName;
    private final String methodName;
    private Object[] parameters;

    public MethodInvocation(String connectorId, String interfaceName,
            String methodName) {
        this.connectorId = connectorId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
    }

    public MethodInvocation(String connectorId, String interfaceName,
            String methodName, Object[] parameters) {
        this(connectorId, interfaceName, methodName);
        setParameters(parameters);
    }

    public String getConnectorId() {
        return connectorId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return connectorId + ":" + interfaceName + "." + methodName + "("
                + Arrays.toString(parameters) + ")";
    }

    /**
     * Gets a String tag that is used to uniquely identify previous method
     * invocations that should be purged from the queue if
     * <code>{@literal @}Delay(lastOnly = true)</code> is used.
     * <p>
     * The returned string should contain at least one non-number char to ensure
     * it doesn't collide with the keys used for invocations without lastOnly.
     * 
     * @return a string identifying this method invocation
     */
    public String getLastOnlyTag() {
        return connectorId + "-" + getInterfaceName() + "-" + getMethodName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodInvocation)) {
            return false;
        }
        MethodInvocation other = (MethodInvocation) obj;
        if (!SharedUtil.equals(getConnectorId(), other.getConnectorId())) {
            return false;
        }

        if (!SharedUtil.equals(getInterfaceName(), other.getInterfaceName())) {
            return false;
        }

        if (!SharedUtil.equals(getMethodName(), other.getMethodName())) {
            return false;
        }

        if (!SharedUtil.equals(getParameters(), other.getParameters())) {
            return false;
        }

        return true;

    }
}
