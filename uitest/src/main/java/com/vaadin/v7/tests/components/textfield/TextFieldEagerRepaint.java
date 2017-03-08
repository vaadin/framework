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
package com.vaadin.v7.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.event.FieldEvents.TextChangeEvent;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.v7.ui.TextField;

public class TextFieldEagerRepaint extends TestBase {

    @Override
    protected void setup() {

        final TextField tf1 = new TextField("Updates value");
        tf1.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf1.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                String text = event.getText();
                if (!text.matches("[a-z]*")) {
                    String newValue = text.replaceAll("[^a-z]", "");
                    tf1.setValue(newValue);
                }
            }
        });

        final TextField tf2 = new TextField("Updates width");
        tf2.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf2.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                String text = event.getText();
                if (!text.matches("[a-z]*")) {
                    tf2.setWidth("100px");
                } else {
                    tf2.setWidth("150px");
                }
            }
        });

        addComponent(tf1);
        addComponent(tf2);
    }

    @Override
    protected String getDescription() {
        return "Updating the value in an EAGER TextChangeListener should send the new value to the client while updating something else (e.g. the width) should preserve the text in the field. Both fields react when the field contains anything else than lower case letters a-z";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6588);
    }

}
