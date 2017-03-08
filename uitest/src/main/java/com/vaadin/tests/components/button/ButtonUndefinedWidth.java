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
package com.vaadin.tests.components.button;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

/**
 * Test UI for buttons with undefined width.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class ButtonUndefinedWidth extends AbstractReindeerTestUI {

    @Override
    protected String getTestDescription() {
        return "Both the button outside the table and inside the table should be only as wide as necessary. There should be empty space in the table to the right of the button.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3257;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Undefined wide");
        addComponent(b);
        NativeButton b2 = new NativeButton("Undefined wide");
        addComponent(b2);

        Table t = new Table();
        t.addContainerProperty("A", Button.class, null);
        t.setWidth("500px");

        Item i = t.addItem("1");
        i.getItemProperty("A").setValue(new Button("Undef wide"));
        Item i2 = t.addItem("2");
        i2.getItemProperty("A").setValue(new NativeButton("Undef wide"));

        addComponent(t);
    }
}
