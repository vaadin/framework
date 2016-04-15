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
package com.vaadin.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Test to see if AbstractOrderedLayout displays captions correctly with
 * expanding ratios.
 * 
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutWithCaptions extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TextField textField = new TextField("Input Text:");
        Label label1 = new Label("LABEL 1");
        Label label2 = new Label("LABEL 2");

        layout.addComponent(textField);

        layout.addComponent(label1);
        layout.setExpandRatio(label1, 1.0f);

        layout.addComponent(label2);

        Panel containingPanel = new Panel(layout);
        containingPanel.setHeight("200px");
        addComponent(containingPanel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test to see if AbstractOrderedLayout calculates captions correctly.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13741;
    }
}
