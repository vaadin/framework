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

package com.vaadin.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.json.JSONArray;
import org.json.JSONException;

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

    // used for sorting calls between different connectors in the same UI
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

    private void writeObject(ObjectOutputStream stream) throws IOException {
        // Need to have custom serialization and deserialization because the
        // constructor allows parameters of any type with Object[]. Thus, having
        // parameters that are not Serializable will lead to
        // NotSerializableException when trying to serialize this class.
        // An example case of this is in #12532 (JavaScriptCallbackHelper ->
        // JSONArray as parameter and not Serializable), for which this
        // hac..workaround is implemented.

        // Goes through the parameter types, and apply "custom serialization" to
        // the ones that are not Serializable by changing them into something
        // that is Serializable. On deserialization (readObject-method below)
        // the process should be reversed.

        // Easy way for implementing serialization & deserialization is by
        // writing/parsing the object's content as string.
        for (int i = 0; i < parameterTypes.length; i++) {
            Type type = parameterTypes[i];
            if (type instanceof Class<?>) {
                Class<?> clazz = (Class<?>) type;
                if (JSONArray.class.isAssignableFrom(clazz)) {
                    parameters[i] = ((JSONArray) parameters[i]).toString();
                }
            }
        }
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException,
            ClassNotFoundException {
        // Reverses the serialization done in writeObject. Basically just
        // parsing the serialized type back to the non-serializable type.
        stream.defaultReadObject();
        for (int i = 0; i < parameterTypes.length; i++) {
            Type type = parameterTypes[i];
            if (type instanceof Class<?>) {
                Class<?> clazz = (Class<?>) type;
                if (JSONArray.class.isAssignableFrom(clazz)) {
                    try {
                        parameters[i] = new JSONArray(((String) parameters[i]));
                    } catch (JSONException e) {
                        throw new IOException(e);
                    }
                }
            }
        }
    }
}
