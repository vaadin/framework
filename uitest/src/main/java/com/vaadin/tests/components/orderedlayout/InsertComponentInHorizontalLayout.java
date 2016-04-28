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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class InsertComponentInHorizontalLayout extends AbstractTestUI {
    private VerticalLayout layout;
    int added = 1;

    private Component getTestLayout() {
        ComboBox a = new ComboBox("initial");
        Button b = new Button("x", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.markAsDirty();
            }
        });
        final HorizontalLayout hl = new HorizontalLayout(a, b);
        hl.setSpacing(true);
        Button add = new Button(
                "Insert 2 comboboxes between combobox(es) and button 'x'");
        add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponent(new ComboBox("Added " + added++), 1);
                hl.addComponent(new ComboBox("Added " + added++), 2);
            }
        });
        layout = new VerticalLayout(hl, add);
        return layout;
    }

    @Override
    protected void setup(VaadinRequest request) {
        setContent(getTestLayout());
    }

    @Override
    protected String getTestDescription() {
        return "Click the button to add two comboboxes between the existing combobox(es) and the 'x' button";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10154;
    }
}
