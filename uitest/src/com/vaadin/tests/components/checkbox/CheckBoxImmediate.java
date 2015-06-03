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
package com.vaadin.tests.components.checkbox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;

public class CheckBoxImmediate extends AbstractTestUI {
    private int count = 0;

    @Override
    protected void setup(VaadinRequest request) {
        final Label status = new Label("Events received: " + count);
        status.setId("count");
        addComponent(status);

        CheckBox cb = new CheckBox("Non-immediate");
        ValueChangeListener listener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                count++;
                status.setValue("Events received: " + count);
            }
        };
        cb.addValueChangeListener(listener);
        cb.setImmediate(false);
        addComponent(cb);

        cb = new CheckBox("Immediate");
        cb.addValueChangeListener(listener);
        cb.setImmediate(true);
        addComponent(cb);
    }

    @Override
    protected String getTestDescription() {
        return "Test for verifying that a non-immediate CheckBox does not send value change to server immediately.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18102;
    }

}
