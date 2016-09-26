/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;

public class StateChangeCounter extends AbstractTestUI {

    @Override
    public String getTestDescription() {
        return "onStateChange should be called only if the state has actually changed";
    }

    @Override
    protected void setup(VaadinRequest request) {
        StateChangeCounterComponent counter = new StateChangeCounterComponent();

        addComponents(new Button("Send RPC", event -> counter.sendRpc()),
                new Button("Change state", event -> counter.changeState()),
                new Button("Mark as dirty", event -> counter.markAsDirty()),
                counter);
    }

    @JavaScript("StateChangeCounter.js")
    public static class StateChangeCounterComponent
            extends AbstractJavaScriptComponent {
        public void sendRpc() {
            callFunction("sendRpc");
        }

        public void changeState() {
            getState().stateCounter++;
        }

        @Override
        protected StateChangeCounterState getState() {
            return (StateChangeCounterState) super.getState();
        }
    }

    public static class StateChangeCounterState
            extends JavaScriptComponentState {
        public int stateCounter = 0;
    }

}
