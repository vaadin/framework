/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.combobox;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

/**
 * Test UI to verify that focus event actually update the ComboBox suggestion
 * popup
 * 
 * @author Vaadin Ltd
 */
public class ComboBoxItemAddingWithFocusListener extends AbstractTestUI {

    private ComboBox cBox;

    @Override
    protected void setup(VaadinRequest request) {
        cBox = new ComboBox();
        addComponent(cBox);
        cBox.setImmediate(true);
        cBox.addItem("Foo");
        cBox.addItem("Bar");
        cBox.addFocusListener(new FocusListener() {

            int x = 0;

            @Override
            public void focus(FocusEvent event) {
                cBox.addItem("Focus" + (x++));
            }

        });
        addComponent(new Button("Focus Target"));
    }

    @Override
    protected String getTestDescription() {
        return "Item adding in focus listener causes popup to clear";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13635;
    }

}
