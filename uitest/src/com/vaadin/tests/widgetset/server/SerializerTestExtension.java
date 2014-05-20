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

package com.vaadin.tests.widgetset.server;

import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.tests.widgetset.client.SerializerTestRpc;
import com.vaadin.tests.widgetset.client.SerializerTestState;

public class SerializerTestExtension extends AbstractExtension {

    @Override
    public <T extends ClientRpc> T getRpcProxy(Class<T> rpcInterface) {
        return super.getRpcProxy(rpcInterface);
    }

    @Override
    public SerializerTestState getState() {
        return (SerializerTestState) super.getState();
    }

    public void registerRpc(SerializerTestRpc rpc) {
        super.registerRpc(rpc);
    }

}
