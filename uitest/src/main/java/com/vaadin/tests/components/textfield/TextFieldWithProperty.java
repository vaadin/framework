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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("unchecked")
public class TextFieldWithProperty extends TestBase {

    @Override
    protected void setup() {

        final TextField tf1 = new TextField();

        final ObjectProperty<String> op = new ObjectProperty<>("FOO");

        tf1.setPropertyDataSource(op);

        addComponent(tf1);

        Button b = new Button(
                "Set BAR to underlaying property (should propagate to UI)");
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                op.setValue("BAR");
            }
        });
        addComponent(b);

    }

    @Override
    protected String getDescription() {
        return "Should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6588;
    }

}
