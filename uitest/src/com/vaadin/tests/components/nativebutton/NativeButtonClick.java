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
package com.vaadin.tests.components.nativebutton;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

/**
 * UI used to validate click coordinates reported from clicks on NativeButton
 * elements.
 * 
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class NativeButtonClick extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Label label1 = new Label("0,0");
        final Label label2 = new Label("0,0");

        Button button1 = new NativeButton("Button1",
                new NativeButton.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        label1.setValue(event.getClientX() + ","
                                + event.getClientY());
                    }
                });
        Button button2 = new NativeButton("Button2",
                new NativeButton.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        label2.setValue(event.getClientX() + ","
                                + event.getClientY());
                    }
                });

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponents(button1, button2, label1, label2);
        layout.setSpacing(true);
        addComponent(layout);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Validate click event coordinates not erroneously returned as x=0, y=0";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14022;
    }

}
