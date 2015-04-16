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
package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.PageClientRpc;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class RpcInvocationHandlerToString extends AbstractTestUIWithLog {

    PageClientRpc dummyProxy = getRpcProxy(PageClientRpc.class);

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Exec toString() for an invocation proxy",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log("An invoation proxy: " + dummyProxy.toString());
                    }
                });
        addButton("Exec hashCode() for an invocation proxy",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log("Invocation proxy.hashCode(): "
                                + dummyProxy.hashCode());
                    }
                });
        addButton("Exec equals(false) for an invocation proxy",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log("Invocation proxy.equals(false): "
                                + dummyProxy.equals(false));
                    }
                });
    }

    @Override
    protected String getTestDescription() {
        return "Clicking on the buttons invokes Object methods on a dummy proxy instance. They should only cause log rows to appear and no client rpc to be sent";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9802;
    }

}
