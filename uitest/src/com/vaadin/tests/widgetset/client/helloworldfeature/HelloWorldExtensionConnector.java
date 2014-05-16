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
package com.vaadin.tests.widgetset.client.helloworldfeature;

import com.google.gwt.user.client.Window;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.extensions.HelloWorldExtension;

@Connect(HelloWorldExtension.class)
public class HelloWorldExtensionConnector extends AbstractExtensionConnector {

    @Override
    public HelloWorldState getState() {
        return (HelloWorldState) super.getState();
    }

    @Override
    protected void init() {
        registerRpc(GreetAgainRpc.class, new GreetAgainRpc() {
            @Override
            public void greetAgain() {
                greet();
            }
        });
    }

    @Override
    protected void extend(ServerConnector target) {
        greet();
    }

    private void greet() {
        String msg = getState().getGreeting() + " from "
                + Util.getConnectorString(this) + " attached to "
                + Util.getConnectorString(getParent());
        VConsole.log(msg);

        String response = Window.prompt(msg, "");
        getRpcProxy(HelloWorldRpc.class).onMessageSent(response);
    }
}
