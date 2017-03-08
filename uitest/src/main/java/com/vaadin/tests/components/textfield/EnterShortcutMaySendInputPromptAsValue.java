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
package com.vaadin.tests.components.textfield;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.TextField;

public class EnterShortcutMaySendInputPromptAsValue extends TestBase {

    @Override
    protected String getDescription() {
        return "?";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2935;
    }

    @Override
    protected void setup() {

        final TextField testField = new TextField();
        testField.setInputPrompt("Enter a value");

        getMainWindow().addActionHandler(new Action.Handler() {

            final Action enter = new ShortcutAction("enter",
                    ShortcutAction.KeyCode.ENTER, null);

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { enter };
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                if (action == enter) {

                }
            }

        });
        testField.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String value = event.getProperty().getValue().toString();
                addComponent(new Label("TextField sent value: " + value));
                testField.setValue("");
            }
        });

        addComponent(testField);

    }

}
