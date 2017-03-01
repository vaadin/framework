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
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Constants;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;

@JavaScript("JavaScriptStateTracking.js")
@Widgetset(Constants.DEFAULT_WIDGETSET)
public class JavaScriptStateTracking extends AbstractTestUI {

    public static class StateTrackingComponentState
            extends JavaScriptComponentState {
        public int counter = 0;
        public String field1 = "initial value";
        public String field2;
    }

    public static class StateTrackingComponent
            extends AbstractJavaScriptComponent {
        @Override
        protected StateTrackingComponentState getState() {
            return (StateTrackingComponentState) super.getState();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        StateTrackingComponent stateTrackingComponent = new StateTrackingComponent();

        Button setField2 = new Button("Set field2", e -> {
            stateTrackingComponent.getState().counter++;
            stateTrackingComponent.getState().field2 = "updated value "
                    + stateTrackingComponent.getState().counter;
        });
        setField2.setId("setField2");

        Button clearField1 = new Button("Clear field1", e -> {
            stateTrackingComponent.getState().counter++;
            stateTrackingComponent.getState().field1 = null;
        });
        clearField1.setId("clearField1");

        addComponents(stateTrackingComponent, setField2, clearField1);
    }

}
