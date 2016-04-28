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
package com.vaadin.tests.components.formlayout;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Test UI for H2 label inside FormLayout.
 * 
 * @author Vaadin Ltd
 */
@Theme("valo")
public class FormLayoutInVerticalLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CssLayout container = new CssLayout();
        addComponent(container);

        FormLayout formLayout = new FormLayout();

        Label sectionLabel = createLabel();
        formLayout.addComponent(sectionLabel);

        TextField nameTextField = new TextField("Name");
        nameTextField.setValue("Lorem ipsum");
        nameTextField.setWidth("50%");
        formLayout.addComponent(nameTextField);

        container.addComponent(formLayout);
        container.addComponent(createLabel());
    }

    @Override
    protected Integer getTicketNumber() {
        return super.getTicketNumber();
    }

    @Override
    protected String getTestDescription() {
        return "FormLayout 'margin-top' value should take precedence over "
                + "the rule defined in any other selector.";
    }

    private Label createLabel() {
        Label sectionLabel = new Label("Personal info");
        sectionLabel.addStyleName(ValoTheme.LABEL_H2);
        sectionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        return sectionLabel;
    }
}
