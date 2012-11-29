/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class InsertRowInMiddle extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final GridLayout layout = new GridLayout(1, 2);
        layout.addComponent(new Label("some row"), 0, 0);
        Button newRowButton = new Button("Insert Row");
        newRowButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                layout.insertRow(1);
                layout.addComponent(new Label("some new row"), 0, 1);
            }
        });
        layout.addComponent(newRowButton, 0, 1);
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "A new row added to the middle of a GridLayout should appear without any exception being thrown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10097);
    }

}
