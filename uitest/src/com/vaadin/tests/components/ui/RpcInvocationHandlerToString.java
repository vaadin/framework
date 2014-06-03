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
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class RpcInvocationHandlerToString extends AbstractTestUI {

    private Log log = new Log(5);
    PageClientRpc dummyProxy = getRpcProxy(PageClientRpc.class);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        Button b = new Button("Exec toString() for an invocation proxy",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log("An invoation proxy: " + dummyProxy.toString());
                    }
                });
        addComponent(b);
        b = new Button("Exec hashCode() for an invocation proxy",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log("Invocation proxy.hashCode(): "
                                + dummyProxy.hashCode());
                    }
                });
        addComponent(b);
        b = new Button("Exec equals(false) for an invocation proxy",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log("Invocation proxy.equals(false): "
                                + dummyProxy.equals(false));
                    }
                });
        addComponent(b);
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
