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
package com.vaadin.terminal.gwt.client.communication;

import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.metadata.Type;

public abstract class RpcMethod {
    private String interfaceName;
    private String methodName;
    private Type[] parameterTypes;

    public RpcMethod(String interfaceName, String methodName,
            Type... parameterTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    public abstract void applyInvocation(ClientRpc target, Object... parameters);

}
