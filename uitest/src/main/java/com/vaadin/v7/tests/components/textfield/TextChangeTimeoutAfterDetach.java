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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.v7.event.FieldEvents.TextChangeEvent;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.v7.ui.TextField;

public class TextChangeTimeoutAfterDetach extends TestBase {

    @Override
    protected void setup() {
        final TextField field = new TextField();
        field.setImmediate(false);
        field.setTextChangeTimeout(2000);
        field.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
        field.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                // Need to add a listener for events to occur
            }
        });
        addComponent(field);

        Button detachBtn = new Button("detach field",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        removeComponent(field);
                        getLayout().addComponentAsFirst(
                                new Label("Field detached!"));
                    }
                });
        addComponent(detachBtn);
    }

    @Override
    protected String getDescription() {
        return "The textfield has a TextChangeTimout of 1 second. Edit the field and immidietly detach the field and you will cause an \"Out Of Sync\" error.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6507;
    }

}
