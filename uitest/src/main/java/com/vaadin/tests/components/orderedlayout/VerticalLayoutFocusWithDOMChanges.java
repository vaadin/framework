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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class VerticalLayoutFocusWithDOMChanges extends AbstractTestUI implements
        ValueChangeListener {

    Button dummyButton = new Button("Just a button");
    TextField listenedTextField = new TextField();
    TextField changingTextField = new TextField();

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        setSizeFull();
        listenedTextField.addValueChangeListener(this);
        listenedTextField.setImmediate(true);
        changingTextField.setImmediate(true);
        content.addComponent(dummyButton);
        content.addComponent(listenedTextField);
        content.addComponent(changingTextField);
        content.setMargin(true);
        content.setSpacing(true);
        setContent(content);
    }

    @Override
    protected String getTestDescription() {
        return "Check that creating or removing caption wrap doesn't lose focus";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12967;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        changingTextField.setRequired(!changingTextField.isRequired());
    }

}
