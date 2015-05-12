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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

/**
 * Test UI for availability (x,y) coordinates for button activated via keyboard.
 * 
 * @author Vaadin Ltd
 */
public class ButtonKeyboardClick extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label[] labels = new Label[4];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
        }

        Button button = new Button("button", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Label label = new Label(String.valueOf(event.getClientX()));
                label.addStyleName("x");
                getLayout().replaceComponent(labels[0], label);
                labels[0] = label;

                label = new Label(String.valueOf(event.getClientY()));
                label.addStyleName("y");
                getLayout().replaceComponent(labels[1], label);
                labels[1] = label;

                label = new Label(String.valueOf(event.getRelativeX()));
                label.addStyleName("xRelative");
                getLayout().replaceComponent(labels[2], label);
                labels[2] = label;

                label = new Label(String.valueOf(event.getRelativeY()));
                label.addStyleName("yRelative");
                getLayout().replaceComponent(labels[3], label);
                labels[3] = label;
            }
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Set client coordinates to the middle of the button when click is triggered from keyboard";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12650;
    }

}
