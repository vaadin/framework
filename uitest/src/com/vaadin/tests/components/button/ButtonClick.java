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
package com.vaadin.tests.components.button;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ButtonClick extends AbstractTestUI {

    public final static String SUCCESS_TEXT = "Click received succesfully!";
    public final static String WRONG_BUTTON_TEXT = "Wrong button clicked.";

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout rootLayout = new VerticalLayout();
        final Label statusLabel = new Label("Test initialized");
        rootLayout.addComponent(new Button("Click here", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                statusLabel.setValue(SUCCESS_TEXT);
            }

        }));
        Button visitLocation = new Button("Drag here", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                statusLabel.setValue(WRONG_BUTTON_TEXT);
            }

        });
        rootLayout.addComponent(statusLabel);
        rootLayout.addComponent(visitLocation);
        rootLayout.setComponentAlignment(visitLocation, Alignment.BOTTOM_RIGHT);
        rootLayout.setSizeFull();
        rootLayout.setMargin(true);
        setContent(rootLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Verify button click logic";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13550;
    }

}
