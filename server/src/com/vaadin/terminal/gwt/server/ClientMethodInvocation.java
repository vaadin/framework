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

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Internal class for keeping track of pending server to client method
 * invocations for a Connector.
 * 
 * @since 7.0
 */
public class ClientMethodInvocation implements Serializable,
        Comparable<ClientMethodInvocation> {
    private final ClientConnector connector;
    private final String interfaceName;
    private final String methodName;
    private final Object[] parameters;
    private Type[] parameterTypes;

    // used for sorting calls between different connectors in the same Root
    private final long sequenceNumber;
    // TODO may cause problems when clustering etc.
    private static long counter = 0;

    public ClientMethodInvocation(ClientConnector connector,
            String interfaceName, Method method, Object[] parameters) {
        this.connector = connector;
        this.interfaceName = interfaceName;
        methodName = method.getName();
        parameterTypes = method.getGenericParameterTypes();
        this.parameters = (null != parameters) ? parameters : new Object[0];
        sequenceNumber = ++counter;
    }

    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    public ClientConnector getConnector() {
        return connector;
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

    protected long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public int compareTo(ClientMethodInvocation o) {
        if (null == o) {
            return 0;
        }
        return Long.signum(getSequenceNumber() - o.getSequenceNumber());
    }
}