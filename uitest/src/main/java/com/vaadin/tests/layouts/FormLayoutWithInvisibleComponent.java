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
package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;

public class FormLayoutWithInvisibleComponent extends TestBase {

    private TextArea messages;

    @Override
    protected String getDescription() {
        return "There is an initial invisible text field below the checkbox. Checking the checkbox should show the field as a textarea (40x10) and also show its caption(\"Messages visible\") and a required error (*).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2706;
    }

    @Override
    protected void setup() {
        FormLayout formLayout = new FormLayout();
        CheckBox control = new CheckBox("Messages On/Off");
        control.addValueChangeListener(event -> {
            messages.setVisible(event.getValue());
            messages.setRequiredIndicatorVisible(true);
            messages.setCaption("Messages visible");
        });
        formLayout.addComponent(control);

        messages = new TextArea("Messages hidden");
        messages.setRows(10);
        messages.setWidth("40em");
        messages.setVisible(false);
        messages.setEnabled(false);
        formLayout.addComponent(messages);

        addComponent(formLayout);
    }

}
