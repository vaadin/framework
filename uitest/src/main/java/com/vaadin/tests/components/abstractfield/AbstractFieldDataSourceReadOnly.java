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
package com.vaadin.tests.components.abstractfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.TextField;

public class AbstractFieldDataSourceReadOnly extends TestBase {

    private static class StateHolder {
        private ObjectProperty<String> textField = new ObjectProperty<>("");

        public ObjectProperty<String> getTextField() {
            return textField;
        }

        @SuppressWarnings("unused")
        public void setTextField(ObjectProperty<String> textField) {
            this.textField = textField;
        }

        public void buttonClicked() {
            textField.setReadOnly(true);
        }
    }

    @Override
    protected void setup() {
        final StateHolder stateHolder = new StateHolder();

        // Button
        Button button = new Button("Make data source read-only");
        button.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                stateHolder.buttonClicked();
            }
        });

        // Input field
        TextField input = new TextField("Field");
        input.setPropertyDataSource(stateHolder.getTextField());

        addComponent(button);
        addComponent(input);
    }

    @Override
    protected String getDescription() {
        return "Read-only status changes in data sources are not rendered immediately";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5013;
    }

}
