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
package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.PageClientRpc;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class RpcInvocationHandlerToString extends AbstractTestUI {

    private Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        Button b = new Button("Show toString() for a invocation proxy",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        PageClientRpc dummyProxy = getRpcProxy(PageClientRpc.class);
                        log.log("An invoation proxy: " + dummyProxy.toString());
                    }
                });
        addComponent(b);
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
